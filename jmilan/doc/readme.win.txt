������ ����������� JMilan
==========================

��� ������ JMilan ������������� ������������ Sun JDK 1.6 [1]. ����� ������
������ Sun JDK �� ��������, ��������� � JMilan ������������ ������������
���� (enum). ����������� ������ JMilan � ������� JDK ������ �����������,
� ��� ����� � ������� OpenJDK, �� �����������.

��� ���������� JMilan �������������� ��������������� ����� NetBeans 6.8 [2].
��� ���������� NetBeans ����� ������� JMilan, ��������� Ant [3].

����� ������� JMilan � ������� NetBeans:

 1. �������� ������ JMilan � NetBeans
 2. �������� ������ � ������� Run -> Build Project

����� ������� JMilan � ������� Ant:

 1. ��������� � ������� ������� JMilan
 2. � ���� �������� ��������� �������

    ant jar

� ���������� ������ � ����������� dist �������� � �������� ������
���������� ��������� ����� � ��������:

JMilan\dist\
  - lib\
      - args-1.0.zip
  - JMilan.jar
  - README.txt

��� ������� ����������� � ������ ������� ��������� �������

  java -jar jmilan.jar -h

����� �����������:

java -jar jmilan.tar -h
java -jar jmilan.jar [-o file] input-file

  -h       ������ ������� � ������� ������ �����������
  -o file  ������ ����������� � ���� file, � �� � ����������� ����� ������

� �������� JMilan\test ��������� ����� �������� ��������
(����� � ����������� .mil), ������� ������ ��� ������ ��������������� JMilan.

��� ���������� ����������� � ������� JMilan ���� ����� ���������������
����������� ������� ������ [4].

�����������
============

���������� JMilan ���������� ���������� Args:

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

� ����������� JMilan ������������ ��������� ���� � ��������� ���� ��������
����������� j-- [5], (c) Bill Campbell, Swami Iyer, Bahar Akbal.

������
=======

[1] http://java.sun.com/javase/downloads/index.jsp
[2] http://netbeans.org/
[3] http://ant.apache.org/
[4] http://dcn.infos.ru/~dtim/software/milan/
[5] http://www.cs.umb.edu/~wrc/cs651/spr09/index.html

�����
======

��������� ������� ����
������� ��������
dtim@dcn.ftk.spbstu.ru

