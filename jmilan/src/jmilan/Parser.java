package jmilan;

import java.io.PrintStream;
import java.util.Hashtable;

/**
 * Синтаксический анализатор языка Милан.
 *
 * Парсер с помощью переданного ему при инициализации лексического
 * анализатора читает по одной лексеме и на основе грамматики
 * Милана генерирует код для стековой виртуальной машины.
 * Синтаксический анализ выполняется методом рекурсивного спуска.
 *
 * При обнаружении ошибки парсер печатает сообщение и продолжает анализ
 * со следующего оператора, чтобы в процессе разбора найти как можно больше
 * ошибок. Поскольку стратегия восстановления после ошибки очень проста,
 * возможна печать сообщений о несуществующих ("наведенных") ошибках
 * или пропуск некоторых ошибок без печати сообщений.
 * Если в процессе разбора была найдена хотя бы одна ошибка,
 * код для виртуальной машины не печатается.
 *
 */

public class Parser {
    /**
     * Конструктор создает парсер, который использует переданный в качестве
     * параметра конструктора лексический анализатор.
     *
     * @param scanner
     *                лексический анализатор, возвращающий очередную
     *                лексему из потока.
     * @param output
     *                выходной поток, куда будет напечатан код для
     *                виртуальной машины.
     */
    public Parser(Scanner scanner, PrintStream output) {
        this.input = scanner;
        this.emitter = new CodeEmitter(output);
        this.variables = new Hashtable<String, Integer>();
        this.nextVariableAddress = 0;
        this.isRecovered = true;
        this.error = false;
        input.nextToken();
    }

    /**
     * Запуск синтаксического анализа.
     */

    public void parse() {
        program();
        mustBe(Token.EOF);
        if(!this.error) {
            this.emitter.flush();
        }
    }

    /**
     * Разбор программы (BEGIN <список операторов> END).
     */

    private void program() {
        mustBe(Token.BEGIN);
        statementList();
        mustBe(Token.END);

        // В конце программы должна быть инструкция остановки.
        emitter.emit(CodeEmitter.Opcode.STOP);
    }

    /**
     * Разбор списка операторов.
     *
     * @return Объект класса Block, содержащий синтаксическое дерево
     *         списка операторов (список синтаксических деревьев
     *         операторов или null, если список операторов пуст).
     */

    private void statementList() {
        /*
         * Если список операторов пуст, очередной лексемой будет
         * одна из возможных "закрывающих скобок": END, OD, ELSE, FI.
         * Эти лексемы образуют множество FOLLOW(statementList).
         *
         * BEGIN <список операторов> END
         * WHILE DO <список операторов> OD
         * IF .. THEN <список операторов> ELSE <список операторов> FI
         *
         * В этом случае результатом разбора будет пустой блок
         * (его список операторов равен null).
         *
         * Если очередная лексема не входит в FOLLOW(statementList),
         * то ее мы считаем началом оператора и вызываем метод
         * statement. Признаком последнего оператора является
         * отсутствие после оператора точки с запятой.
         */

        if(see(Token.END) || see(Token.OD) || see(Token.ELSE) || see(Token.FI)) {
            return; // Список операторов пуст
        }
        else {
            boolean more = true;
            while(more) {
                statement();
                more = match(Token.SEMICOLON);
            }
        }
    }

    /**
     * Разбор оператора.
     *
     */

    private void statement() {
        /*
         * Оператор может быть:
         * - оператором присваивания:
         *   <идентификатор> := <выражение>
         * - условным оператором:
         *   if <условие> then <список операторов> [else <список операторов>] fi
         * - оператором цикла:
         *   while <условие> do <список операторов> od
         * - оператором печати
         *   write '(' <выражение> ')'
         *
         * Метод проверяет, что текущая лексема входит в множество
         *   FIRST(statement) = { IDENTIFIER, IF, WHILE, WRITE },
         * и в зависимости от ее значения выбирает правило разбора.
         * Если лексема ни совпадает ни с одним допустимым значением,
         * формируется сообщение об ошибке.
         */

        if(match(Token.IDENTIFIER)) {
            // Запомним адрес переменной, соответствующей прочитанному
            // идентификатору.
            int var = getVariableAddress(input.stringValue());

            mustBe(Token.ASSIGN);
            expression();

            // Код: записать значение с верхушки стека в ячейку
            // памяти, адрес которой соответствует идентификатору
            // в левой части оператора присваивания.
            emitter.emit(CodeEmitter.Opcode.STORE, var);
        }
        else if(match(Token.IF)) {
            relationalExpression();

            // Код: зарезервируем инструкцию для условного перехода
            // к блоку ELSE, который должен быть выполнен, если
            // условие окажется ложным. Адрес, куда нужно перейти,
            // не будет известен до тех пор, пока мы не сгенерируем
            // код для блока THEN; после этого мы заполним зарезервированное
            // место командой перехода.

            int jump_no = emitter.makeHole();

            mustBe(Token.THEN);
            statementList();
            if(match(Token.ELSE)) {
                // Код: в условном операторе есть блок ELSE. Чтобы не попасть
                // в него после исполнения блока THEN, зарезервируем
                // место для безусловного перехода в конец блока ELSE.
                int jump = emitter.makeHole();

                // Код: теперь известен адрес начала блока ELSE, заполним
                // зарезервированное место после проверки условия инструкцией
                // перехода.
                
                emitter.emitAt(jump_no, CodeEmitter.Opcode.JUMP_NO, emitter.getCurrentAddress());
                statementList();

                // Код: сформируем по второму зарезервированному адресу
                // инструкцию безусловного перехода в конец условного оператора.
                emitter.emitAt(jump, CodeEmitter.Opcode.JUMP, emitter.getCurrentAddress());
            }
            else {
                // Код: блока ELSE нет. Запишем по зарезервированному адресу
                // инструкцию условного перехода в конец условного оператора.
                emitter.emitAt(jump_no, CodeEmitter.Opcode.JUMP_NO, emitter.getCurrentAddress());
            }
            mustBe(Token.FI);
        }
        else if(match(Token.WHILE)) {
            // Код: запомним адрес начала проверки условия, сюда надо будет
            // возвращаться после каждого исполнения тела цикла.
            int cond = emitter.getCurrentAddress();
            relationalExpression();

            // Код: зарезервируем инструкцию для условного перехода, который
            // будет использоваться для выхода из цикла.
            int jump_no = emitter.makeHole();
            mustBe(Token.DO);
            statementList();
            mustBe(Token.OD);

            // Код: сгенерируем в конце тела цикла безусловный переход
            // на проверку условия.
            emitter.emit(CodeEmitter.Opcode.JUMP, cond);

            // Код: запишем по зарезервированному адресу инструкцию
            // условного перехода на следующий за циклом оператор.
            emitter.emitAt(jump_no, CodeEmitter.Opcode.JUMP_NO, emitter.getCurrentAddress());
        }
        else if(match(Token.WRITE)) {
            mustBe(Token.LPAREN);
            expression();
            mustBe(Token.RPAREN);
            emitter.emit(CodeEmitter.Opcode.PRINT);
        }
        else {
            this.error = true;
            reportError("Statement expected, but %s found.",
                    input.token().toString());
        }
    }

    /**
     * Разбор арифметического выражения.
     */

    private void expression() {
        boolean more = true;

        /*
         * Корректное арифметическое выражение состоит по крайней мере
         * из одного терма и имеет вид
         * <term> ( (+|-) <term> )*
         *
         * При разборе сначала разбираем первый терм, затем анализируем
         * очередной символ. Если это '+' или '-', удаляем его из потока и
         * разбираем очередное слагаемое (вычитаемое). Повторяем проверку
         * и чтение очередного терма до тех пор, пока не встретим за термом
         * символ, отличный от '+' и '-'.
         *
         */

        term();
        while(more) {
            if(match(Token.ADDOP)) {
                Arithmetic op = input.arithmeticValue();
                term();

                if(op == Arithmetic.PLUS) {
                    emitter.emit(CodeEmitter.Opcode.ADD);
                }
                else {
                    emitter.emit(CodeEmitter.Opcode.SUB);
                }
            }
            else {
                more = false;
            }
        }
    }

    private void term() {
        boolean more = true;

        factor();
        while(more) {
            if(match(Token.MULOP)) {
                Arithmetic op = input.arithmeticValue();
                factor();

                if(op == Arithmetic.MULTIPLY) {
                    emitter.emit(CodeEmitter.Opcode.MULT);
                }
                else {
                    emitter.emit(CodeEmitter.Opcode.DIV);
                }
            }
            else {
                more = false;
            }
        }
    }

    private void factor() {
        if(match(Token.IDENTIFIER)) {
            emitter.emit(CodeEmitter.Opcode.LOAD, getVariableAddress(this.input.stringValue()));
        }
        else if(match(Token.NUMBER)) {
            emitter.emit(CodeEmitter.Opcode.PUSH, this.input.intValue());
        }
        else if(match(Token.READ)) {
            emitter.emit(CodeEmitter.Opcode.INPUT);
        }
        else if(match(Token.LPAREN)) {
            expression();
            mustBe(Token.RPAREN);
        }
        else if(match(Token.ADDOP) && input.arithmeticValue() == Arithmetic.MINUS) {
            factor();
            emitter.emit(CodeEmitter.Opcode.INVERT);
        }
        else {
            this.error = true;
            reportError("Expected identifier, number, READ, '(' or unary minus, but %s found.",
                    input.token().toString());
        }
    }

    private void relationalExpression() {
        expression();
        if(match(Token.CMP)) {
            expression();
            emitter.emit(CodeEmitter.Opcode.COMPARE,
                    emitter.relationCode(input.cmpValue()));
        }
        else {
            this.error = true;
            reportError("Relational operator required, but %s found.",
                    input.token());
        }
    }

    /**
     * Сравнение очередной лексемы с образцом. Лексема при этом
     * остается в потоке.
     *
     * @param tok Образец
     * @return true, если очередная лексема совпадает с образцом,
     *         false в противном случае.
     */

    private boolean see(Token tok) {
        return input.token() == tok;
    }

    /**
     * Сравнение очередной лексемы с образцом и удаление лексемы из потока
     * в случае совпадения.
     *
     * @param tok Образец
     * @return true, если очередная лексема совпала с образцом (лексема
     *         при этом удаляется из потока),
     *         false в противном случае.
     */

    private boolean match(Token tok) {
        if(input.token() == tok) {
            input.nextToken();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Сравнение очередной лексемы с образцом и генерация сообщения об ошибке
     * в случае несовпадения.
     *
     * Метод предназначен для проверки наличия в потоке "обязательной"
     * лексемы, отсутствие которой означает нарушение правил грамматики.
     *
     * @param tok Образец.
     */
    private void mustBe(Token tok) {
        if(input.token() == tok) {
            input.nextToken();
            isRecovered = true;
        }
        else {
            this.error = true;

            if(isRecovered) {
            isRecovered = false;
            this.error = true;
            reportError("%s found while %s is expected.",
                    input.token().toString(), tok.toString());
            }
            else {
                while(input.token() != tok && input.token() != Token.EOF) {
                    input.nextToken();
                }

                if(input.token() == tok) {
                    input.nextToken();
                    isRecovered = true;
                }
            }
        }
    }

    private void reportError(String message, Object... args) {
        System.err.printf("[%s:%d] : ", input.getFileName(), input.getLineNumber());
        System.err.printf(message, args);
        System.err.println();
    }

    private int getVariableAddress(String name) {
        if(variables.containsKey(name)) {
            return variables.get(name).intValue();
        }
        else {
            variables.put(name, Integer.valueOf(nextVariableAddress));
            return nextVariableAddress++;
        }
    }

    private Scanner input; // Объект лексического анализатора
    private CodeEmitter emitter; // Объект кодогенератора
    private Hashtable<String, Integer> variables; // Таблица переменных
    private int nextVariableAddress; // Первый свободный адрес в области данных
    private boolean isRecovered; // Признак восстановления после ошибки
    private boolean error; // Признак наличия ошибки в анализируемой программе
}
