Сборка компилятора JMilan
==========================

Для сборки JMilan рекомендуется использовать Sun JDK 1.6 [1]. Более ранние
версии Sun JDK не подойдут, поскольку в JMilan используются перечислимые
типы (enum). Возможность сборки JMilan с помощью JDK других поставщиков,
в том числе с помощью OpenJDK, не проверялась.

Для разработки JMilan использовалась интегрированная среда NetBeans 6.8 [2].
При отсутствии NetBeans можно собрать JMilan, используя Ant [3].

Чтобы собрать JMilan с помощью NetBeans:

 1. Откройте проект JMilan в NetBeans
 2. Соберите проект с помощью Run -> Build Project

Чтобы собрать JMilan с помощью Ant:

 1. Перейдите в каталог проекта JMilan
 2. В этом каталоге выполните команду

    ant jar

В результате сборки в подкаталоге dist каталога с проектом должны
находиться следующие файлы и каталоги:

JMilan\dist\
  - lib\
      - args-1.0.zip
  - JMilan.jar
  - README.txt

Для запуска компилятора в режиме справки выполните команду

  java -jar jmilan.jar -h

Ключи компилятора:

java -jar jmilan.tar -h
java -jar jmilan.jar [-o file] input-file

  -h       печать справки о режимах работы компилятора
  -o file  печать результатов в файл file, а не в стандартный поток вывода

В каталоге JMilan\test находится набор тестовых примеров
(файлы с расширением .mil), которые должны без ошибок компилироваться JMilan.

Для выполнения полученного с помощью JMilan кода можно воспользоваться
виртуальной машиной Милана [4].

Уведомления
============

Компилятор JMilan использует библиотеку Args:

  Args - A Reusable Solution for Command Line Arguments Parsing in Java
  Web site: http://www.adarshr.com/papers/args

  Copyright 2008 Adarsh Ramamurthy

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

В компиляторе JMilan использованы некоторые идеи и фрагменты кода учебного
компилятора j-- [5], (c) Bill Campbell, Swami Iyer, Bahar Akbal.

Ссылки
=======

[1] http://java.sun.com/javase/downloads/index.jsp
[2] http://netbeans.org/
[3] http://ant.apache.org/
[4] http://dcn.infos.ru/~dtim/software/milan/
[5] http://www.cs.umb.edu/~wrc/cs651/spr09/index.html

Автор
======

Ассистент кафедры РВКС
Дмитрий Тимофеев
dtim@dcn.ftk.spbstu.ru

