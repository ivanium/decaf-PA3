# <div align="center">Decaf PA3实验报告</div>

<div align="center">计54 乔一凡 2015011398</div>

## 一 实现简介

本次实验首

## 二 功能实现

本部分将说明新增的语法功能，与对应修改的源文件内容。

### 1. 整复数类型

修改文件：

* BaseType中增加COMPLEX类型，使编译器可以识别；

* 修改TypeCheck，在第二轮遍历中计算相应类型。

  * 在visitUnary中添加三个复数类型单目运算符，并修改相应逻辑；

  * 在visitLiteral中加入COMPLEX类别，识别常数；

  * 增加visitPrintComp方法，判断PrintComp参数非复数类型错误；

* 新建BadPrintCompError，表示PrintComp函数参数非复数的错误；

### 2. Case表达式的支持

case表达式使用的产生式较多，修改也较复杂。

* TypeCheck

  * 添加visitCase，在其中对Case的前三种错误进行判断，包括E是整数表达式，Cn互不相同，En类型相同。

  * 添加visitCaseItem，在其中对CaseItem进行合法性检查，检测Constant部分类型是否是整数常量，body部分是否错误；

  * 添加visitDefaultItem，用于获取其表达式的type给case表达式返回。

* 新建IncompatCaseCondError，表示条件类型错误

* 新建CaseBodyDiffError，表示子表达式类型不同错误

* 新建CaseCondNotUniqueError，表示子条件重复错误

### 3. 支持super表达式

* TypeCheck

  * 新增visitSuperExpr,判断是否static调用，并确定类型；

  * visitIdent判断owner是否super类型的判断，抛出访问父类成员的错误；

  * visitCallExpr中判断receiver时super的判断，判断父类中是否有所调用的方法。

* 新建SuperInStaticError，表示super在static函数中被调用的错误

* 新建SuperNoParentError，表示调用super的类没有父类的错误

* 新建SuperNoIdentError，表示访问父类成员的错误

### 4. 支持Dcopy和Scopy

* TypeCheck

  * 新增visitScopy和visitDcopy，判断参数类型是否是class，若不是则报错

  * 修改visitAssign，判断expr是否时DCOPY或者SCOPY，判断COPY语句赋值给的参数类型是否相同

* 新建BadCopyAssignError，表示copy语句赋值时被赋值语句类型不同的错误

* 新建BadCopyClassError，表示copy语句参数不是class

### 5. 串行循环卫士语句

由于循环卫士语句中有stmt，且产生式较多，结构更加复杂，需要修改第一遍遍历。

* BuildSym

  * 重载visitDoStmt和visitDoSubStmt，遍历do语句中所有的stmt，计算属性。

* TypeCheck

  * 重载visitDoStmt，计算属性值，遍历子语句

  * 重载visitDoSubStmt，仿照其他循环语句的实现，实现break的判断；判断条件是否是bool类型。

* 新增BadDoCondError，表示do子语句的条件错误
