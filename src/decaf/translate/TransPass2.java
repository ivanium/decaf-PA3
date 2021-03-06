package decaf.translate;

import java.util.Stack;
import java.util.Iterator;

import decaf.tree.Tree;
import decaf.tree.Tree.CaseItem;
import decaf.tree.Tree.DefaultItem;
import decaf.tree.Tree.DoSubStmt;
import decaf.backend.OffsetCounter;
import decaf.machdesc.Intrinsic;
import decaf.scope.ClassScope;
import decaf.symbol.Variable;
import decaf.symbol.Class;
import decaf.symbol.Symbol;
import decaf.tac.Label;
import decaf.tac.Temp;
import decaf.type.BaseType;
import decaf.type.ClassType;

public class TransPass2 extends Tree.Visitor {

  private Translater tr;

  private Temp currentThis;

  private Stack<Label> loopExits;

  public TransPass2(Translater tr) {
    this.tr = tr;
    loopExits = new Stack<Label>();
  }

  @Override
  public void visitClassDef(Tree.ClassDef classDef) {
    for (Tree f : classDef.fields) {
      f.accept(this);
    }
  }

  @Override
  public void visitMethodDef(Tree.MethodDef funcDefn) {
    if (!funcDefn.statik) {
      currentThis = ((Variable) funcDefn.symbol.getAssociatedScope()
          .lookup("this")).getTemp();
    }
    tr.beginFunc(funcDefn.symbol);
    funcDefn.body.accept(this);
    tr.endFunc();
    currentThis = null;
  }

  @Override
  public void visitTopLevel(Tree.TopLevel program) {
    for (Tree.ClassDef cd : program.classes) {
      cd.accept(this);
    }
  }

  @Override
  public void visitVarDef(Tree.VarDef varDef) {
    if (varDef.symbol.isLocalVar()) {
      Temp t = Temp.createTempI4();
      t.sym = varDef.symbol;
      varDef.symbol.setTemp(t);
    }
  }

  @Override
  public void visitBinary(Tree.Binary expr) {
    expr.left.accept(this);
    expr.right.accept(this);
    switch (expr.tag) {
    case Tree.PLUS:
      if(!expr.left.type.equal(BaseType.COMPLEX) && !expr.right.type.equal(BaseType.COMPLEX)) {
        expr.val = tr.genAdd(expr.left.val, expr.right.val);
      } else {
        expr.val = tr.genComplex();
        Temp re1 = expr.left.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.left.val, 0) : expr.left.val;
        Temp im1 = expr.left.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.left.val, OffsetCounter.WORD_SIZE) : tr.genLoadImm4(0);
        Temp re2 = expr.right.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.right.val, 0) : expr.right.val;
        Temp im2 = expr.right.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.right.val, OffsetCounter.WORD_SIZE) : tr.genLoadImm4(0);

        Temp re = tr.genAdd(re1, re2);
        Temp im = tr.genAdd(im1, im2);
        tr.genStore(re, expr.val, 0);
        tr.genStore(im, expr.val, OffsetCounter.WORD_SIZE);
      }
      break;
    case Tree.MINUS:
      expr.val = tr.genSub(expr.left.val, expr.right.val);
      break;
    case Tree.MUL:
      if(!expr.left.type.equal(BaseType.COMPLEX) && !expr.right.type.equal(BaseType.COMPLEX)) {
        expr.val = tr.genMul(expr.left.val, expr.right.val);
      } else {
        expr.val = tr.genComplex();
        Temp re1 = expr.left.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.left.val, 0) : expr.left.val;
        Temp im1 = expr.left.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.left.val, OffsetCounter.WORD_SIZE) : tr.genLoadImm4(0);
        Temp re2 = expr.right.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.right.val, 0) : expr.right.val;
        Temp im2 = expr.right.type.equal(BaseType.COMPLEX) ? tr.genLoad(expr.right.val, OffsetCounter.WORD_SIZE) : tr.genLoadImm4(0);

        Temp re = tr.genSub(tr.genMul(re1, re2), tr.genMul(im1, im2));
        Temp im = tr.genAdd(tr.genMul(re1, im2), tr.genMul(im1, re2));
        tr.genStore(re, expr.val, 0);
        tr.genStore(im, expr.val, OffsetCounter.WORD_SIZE);
      }
      break;
    case Tree.DIV:
      tr.genDivideByZero(expr.right.val);
      expr.val = tr.genDiv(expr.left.val, expr.right.val);
      break;
    case Tree.MOD:
      tr.genDivideByZero(expr.right.val);
      expr.val = tr.genMod(expr.left.val, expr.right.val);
      break;
    case Tree.AND:
      expr.val = tr.genLAnd(expr.left.val, expr.right.val);
      break;
    case Tree.OR:
      expr.val = tr.genLOr(expr.left.val, expr.right.val);
      break;
    case Tree.LT:
      expr.val = tr.genLes(expr.left.val, expr.right.val);
      break;
    case Tree.LE:
      expr.val = tr.genLeq(expr.left.val, expr.right.val);
      break;
    case Tree.GT:
      expr.val = tr.genGtr(expr.left.val, expr.right.val);
      break;
    case Tree.GE:
      expr.val = tr.genGeq(expr.left.val, expr.right.val);
      break;
    case Tree.EQ:
    case Tree.NE:
      genEquNeq(expr);
      break;
    }
  }

  private void genEquNeq(Tree.Binary expr) {
    if (expr.left.type.equal(BaseType.STRING)
        || expr.right.type.equal(BaseType.STRING)) {
      tr.genParm(expr.left.val);
      tr.genParm(expr.right.val);
      expr.val = tr.genDirectCall(Intrinsic.STRING_EQUAL.label,
          BaseType.BOOL);
      if(expr.tag == Tree.NE){
        expr.val = tr.genLNot(expr.val);
      }
    } else {
      if(expr.tag == Tree.EQ)
        expr.val = tr.genEqu(expr.left.val, expr.right.val);
      else
        expr.val = tr.genNeq(expr.left.val, expr.right.val);
    }
  }

  @Override
  public void visitAssign(Tree.Assign assign) {
    assign.left.accept(this);
    assign.expr.accept(this);
    switch (assign.left.lvKind) {
    case ARRAY_ELEMENT:
      Tree.Indexed arrayRef = (Tree.Indexed) assign.left;
      Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
      Temp t = tr.genMul(arrayRef.index.val, esz);
      Temp base = tr.genAdd(arrayRef.array.val, t);
      tr.genStore(assign.expr.val, base, 0);
      break;
    case MEMBER_VAR:
      Tree.Ident varRef = (Tree.Ident) assign.left;
      tr.genStore(assign.expr.val, varRef.owner.val, varRef.symbol
          .getOffset());
      break;
    case PARAM_VAR:
    case LOCAL_VAR:
      tr.genAssign(((Tree.Ident) assign.left).symbol.getTemp(),
          assign.expr.val);
      break;
    }
  }

  @Override
  public void visitLiteral(Tree.Literal literal) {
    switch (literal.typeTag) {
    case Tree.INT:
      literal.val = tr.genLoadImm4(((Integer)literal.value).intValue());
      break;
    case Tree.IMG:
      literal.val = tr.genComplex();
      Temp zero = tr.genLoadImm4(0);
      Temp img = tr.genLoadImm4(((Integer)literal.value).intValue());
      tr.genStore(zero, literal.val, 0);
      tr.genStore(img, literal.val, OffsetCounter.WORD_SIZE);
      break;
    case Tree.BOOL:
      literal.val = tr.genLoadImm4((Boolean)(literal.value) ? 1 : 0);
      break;
    default:
      literal.val = tr.genLoadStrConst((String)literal.value);
    }
  }

  @Override
  public void visitExec(Tree.Exec exec) {
    exec.expr.accept(this);
  }

  @Override
  public void visitUnary(Tree.Unary expr) {
    expr.expr.accept(this);
    switch (expr.tag){
    case Tree.NEG:
      expr.val = tr.genNeg(expr.expr.val);
      break;
    case Tree.RE:
      expr.val = tr.genLoad(expr.expr.val, 0);
      break;
    case Tree.IM:
      expr.val = tr.genLoad(expr.expr.val, OffsetCounter.WORD_SIZE);
      break;
    case Tree.COMPCAST:
      expr.val = tr.genComplex();
      tr.genStore(expr.expr.val, expr.val, 0);
      tr.genStore(tr.genLoadImm4(0), expr.val, OffsetCounter.WORD_SIZE);
      break;
    case Tree.NOT:
      expr.val = tr.genLNot(expr.expr.val);
      break;
    default:
      break;
    }
  }

  @Override
  public void visitNull(Tree.Null nullExpr) {
    nullExpr.val = tr.genLoadImm4(0);
  }

  @Override
  public void visitBlock(Tree.Block block) {
    for (Tree s : block.block) {
      s.accept(this);
    }
  }

  @Override
  public void visitThisExpr(Tree.ThisExpr thisExpr) {
    thisExpr.val = currentThis;
  }

  @Override
  public void visitSuperExpr(Tree.SuperExpr superExpr) {
    superExpr.val = currentThis;
  }

  @Override
  public void visitReadIntExpr(Tree.ReadIntExpr readIntExpr) {
    readIntExpr.val = tr.genIntrinsicCall(Intrinsic.READ_INT);
  }

  @Override
  public void visitReadLineExpr(Tree.ReadLineExpr readStringExpr) {
    readStringExpr.val = tr.genIntrinsicCall(Intrinsic.READ_LINE);
  }

  @Override
  public void visitReturn(Tree.Return returnStmt) {
    if (returnStmt.expr != null) {
      returnStmt.expr.accept(this);
      tr.genReturn(returnStmt.expr.val);
    } else {
      tr.genReturn(null);
    }

  }

  @Override
  public void visitPrint(Tree.Print printStmt) {
    for (Tree.Expr r : printStmt.exprs) {
      r.accept(this);
      tr.genParm(r.val);
      if (r.type.equal(BaseType.BOOL)) {
        tr.genIntrinsicCall(Intrinsic.PRINT_BOOL);
      } else if (r.type.equal(BaseType.INT)) {
        tr.genIntrinsicCall(Intrinsic.PRINT_INT);
      } else if (r.type.equal(BaseType.STRING)) {
        tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
      }
    }
  }

  @Override
  public void visitPrintComp(Tree.PrintComp printCompStmt) {
    Temp plus = tr.genLoadStrConst("+");
    Temp compj = tr.genLoadStrConst("j");
    for (Tree.Expr r : printCompStmt.exprs) {
      r.accept(this);
      tr.genParm(tr.genLoad(r.val, 0));
      tr.genIntrinsicCall(Intrinsic.PRINT_INT);
      tr.genParm(plus);
      tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
      tr.genParm(tr.genLoad(r.val, OffsetCounter.WORD_SIZE));
      tr.genIntrinsicCall(Intrinsic.PRINT_INT);
      tr.genParm(compj);
      tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
    }
  }
  
  @Override
  public void visitIndexed(Tree.Indexed indexed) {
    indexed.array.accept(this);
    indexed.index.accept(this);
    tr.genCheckArrayIndex(indexed.array.val, indexed.index.val);
    
    Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
    Temp t = tr.genMul(indexed.index.val, esz);
    Temp base = tr.genAdd(indexed.array.val, t);
    indexed.val = tr.genLoad(base, 0);
  }

  @Override
  public void visitIdent(Tree.Ident ident) {
    if(ident.lvKind == Tree.LValue.Kind.MEMBER_VAR){
      ident.owner.accept(this);
    }
    
    switch (ident.lvKind) {
    case MEMBER_VAR:
      ident.val = tr.genLoad(ident.owner.val, ident.symbol.getOffset());
      break;
    default:
      ident.val = ident.symbol.getTemp();
      break;
    }
  }
  
  @Override
  public void visitBreak(Tree.Break breakStmt) {
    tr.genBranch(loopExits.peek());
  }

  @Override
  public void visitCallExpr(Tree.CallExpr callExpr) {
    if (callExpr.isArrayLength) {
      callExpr.receiver.accept(this);
      callExpr.val = tr.genLoad(callExpr.receiver.val,
          -OffsetCounter.WORD_SIZE);
    } else {
      if (callExpr.receiver != null) {
        callExpr.receiver.accept(this);
      }
      for (Tree.Expr expr : callExpr.actuals) {
        expr.accept(this);
      }
      if (callExpr.receiver != null) {
        tr.genParm(callExpr.receiver.val);
      }
      for (Tree.Expr expr : callExpr.actuals) {
        tr.genParm(expr.val);
      }
      if (callExpr.receiver == null) {
        callExpr.val = tr.genDirectCall(
            callExpr.symbol.getFuncty().label, callExpr.symbol
            .getReturnType());
        
      } else {
        // if(callExpr.receiver.tag == Tree.SUPEREXPR) {
        //   // Temp vt = tr.genLoadVTable(((ClassType)(callExpr.receiver.type)).getSymbol().getVtable());
        //   Temp vt = tr.genLoadVTable(((ClassType)(callExpr.receiver.type)).getParentType().getSymbol().getVtable());
        //   Temp func = tr.genLoad(vt, callExpr.symbol.getOffset());
        //   callExpr.val = tr.genIndirectCall(func, callExpr.symbol.getReturnType());
        // } else {
        //   Temp vt = tr.genLoadVTable(((ClassType)(callExpr.receiver.type)).getSymbol().getVtable());
        //   // Temp vt = tr.genLoad(callExpr.receiver.val, 0);
        //   Temp func = tr.genLoad(vt, callExpr.symbol.getOffset());
        //   callExpr.val = tr.genIndirectCall(func, callExpr.symbol
        //       .getReturnType());
        // }
        Temp vt;
        // if(callExpr.receiver.tag == Tree.SUPEREXPR) {
          vt = tr.genLoadVTable(((ClassType)(callExpr.receiver.type)).getSymbol().getVtable());
        // } else {
          // vt = tr.genLoadVTable(((ClassType)(callExpr.receiver.type)).getSymbol().getVtable());
        // }
        Temp func = tr.genLoad(vt, callExpr.symbol.getOffset());
        callExpr.val = tr.genIndirectCall(func, callExpr.symbol
            .getReturnType());
      }
    }
  }

  @Override
  public void visitForLoop(Tree.ForLoop forLoop) {
    if (forLoop.init != null) {
      forLoop.init.accept(this);
    }
    Label cond = Label.createLabel();
    Label loop = Label.createLabel();
    tr.genBranch(cond);
    tr.genMark(loop);
    if (forLoop.update != null) {
      forLoop.update.accept(this);
    }
    tr.genMark(cond);
    forLoop.condition.accept(this);
    Label exit = Label.createLabel();
    tr.genBeqz(forLoop.condition.val, exit);
    loopExits.push(exit);
    if (forLoop.loopBody != null) {
      forLoop.loopBody.accept(this);
    }
    tr.genBranch(loop);
    loopExits.pop();
    tr.genMark(exit);
  }

  @Override
  public void visitIf(Tree.If ifStmt) {
    ifStmt.condition.accept(this);
    if (ifStmt.falseBranch != null) {
      Label falseLabel = Label.createLabel();
      tr.genBeqz(ifStmt.condition.val, falseLabel);
      ifStmt.trueBranch.accept(this);
      Label exit = Label.createLabel();
      tr.genBranch(exit);
      tr.genMark(falseLabel);
      ifStmt.falseBranch.accept(this);
      tr.genMark(exit);
    } else if (ifStmt.trueBranch != null) {
      Label exit = Label.createLabel();
      tr.genBeqz(ifStmt.condition.val, exit);
      if (ifStmt.trueBranch != null) {
        ifStmt.trueBranch.accept(this);
      }
      tr.genMark(exit);
    }
  }

  @Override
  public void visitNewArray(Tree.NewArray newArray) {
    newArray.length.accept(this);
    newArray.val = tr.genNewArray(newArray.length.val);
  }

  @Override
  public void visitNewClass(Tree.NewClass newClass) {
    newClass.val = tr.genDirectCall(newClass.symbol.getNewFuncLabel(),
        BaseType.INT);
  }

  @Override
  public void visitWhileLoop(Tree.WhileLoop whileLoop) {
    Label loop = Label.createLabel();
    tr.genMark(loop);
    whileLoop.condition.accept(this);
    Label exit = Label.createLabel();
    tr.genBeqz(whileLoop.condition.val, exit);
    loopExits.push(exit);
    if (whileLoop.loopBody != null) {
      whileLoop.loopBody.accept(this);
    }
    tr.genBranch(loop);
    loopExits.pop();
    tr.genMark(exit);
  }

  @Override
  public void visitCase(Tree.Case caseExpr) {
    caseExpr.condition.accept(this);
    caseExpr.val = Temp.createTempI4();
    Label exit = Label.createLabel();
    for (Tree cs : caseExpr.caseList) {
      Label next = Label.createLabel();
      Tree.CaseItem e = ((CaseItem)cs);
      e.constant.accept(this);
      Temp t = tr.genSub(caseExpr.condition.val, e.constant.val);
      tr.genBnez(t, next);
      if(e.body != null) {
        e.body.accept(this);
      }
      tr.genAssign(caseExpr.val, e.body.val);
      tr.genBranch(exit);
      tr.genMark(next);
    }
    Tree.DefaultItem d = (DefaultItem)caseExpr.defaultItem;
    if(d.body != null) {
      d.body.accept(this);
    }
    tr.genAssign(caseExpr.val, d.body.val);
    tr.genMark(exit);
  }

  @Override
  public void visitDoStmt(Tree.DoStmt doStmt) {
    Label loop = Label.createLabel();
    tr.genMark(loop);
    Label exit = Label.createLabel();
    for(Tree s : doStmt.doBody) {
      Label next = Label.createLabel();
      Tree.DoSubStmt ds = ((DoSubStmt)s);
      ds.cond.accept(this);
      tr.genBeqz(ds.cond.val, next);
      loopExits.push(exit);
      if(ds.body != null) {
        ds.body.accept(this);
      }
      tr.genBranch(loop);
      loopExits.pop();
      tr.genMark(next);
    }
    tr.genMark(exit);
  }

  @Override
  public void visitDcopy(Tree.Dcopy dcopy) {
    dcopy.dcopyExpr.accept(this);
    Class c = ((ClassType)dcopy.dcopyExpr.type).getSymbol();
    Temp loc = dcopy.dcopyExpr.val;
    Temp newObj = tr.genFuncForDcopy(c, loc);
    dcopy.val = newObj;
  }

  @Override
  public void visitScopy(Tree.Scopy scopy) {
    scopy.scopyExpr.accept(this);
    Class c = ((ClassType)scopy.scopyExpr.type).getSymbol();
    Temp size = tr.genLoadImm4(c.getSize());
    tr.genParm(size);
    Temp newObj = tr.genIntrinsicCall(Intrinsic.ALLOCATE);
    int time = c.getSize() / OffsetCounter.WORD_SIZE;
    if (time != 0) {
      Temp oldObj = scopy.scopyExpr.val;
      Temp zero = tr.genLoadImm4(0);
      if (time < 5) {
        for (int i = 0; i < time; i++) {
          tr.genStore(tr.genLoad(oldObj, OffsetCounter.WORD_SIZE * i), newObj, OffsetCounter.WORD_SIZE * i);
        }
      } else {
        Temp unit = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
        Label loop = Label.createLabel();
        Label exit = Label.createLabel();
        newObj = tr.genAdd(newObj, size);
        oldObj = tr.genAdd(oldObj, size);
        tr.genMark(loop);
        tr.genAssign(newObj, tr.genSub(newObj, unit));
        tr.genAssign(oldObj, tr.genSub(oldObj, unit));
        tr.genAssign(size, tr.genSub(size, unit));
        tr.genBeqz(size, exit);
        tr.genStore(tr.genLoad(oldObj, 0), newObj, 0);
        tr.genBranch(loop);
        tr.genMark(exit);
      }
    }
    tr.genStore(tr.genLoadVTable(c.getVtable()), newObj, 0);
    scopy.val = newObj;
  }

  @Override
  public void visitTypeTest(Tree.TypeTest typeTest) {
    typeTest.instance.accept(this);
    typeTest.val = tr.genInstanceof(typeTest.instance.val,
        typeTest.symbol);
  }

  @Override
  public void visitTypeCast(Tree.TypeCast typeCast) {
    typeCast.expr.accept(this);
    if (!typeCast.expr.type.compatible(typeCast.symbol.getType())) {
      tr.genClassCast(typeCast.expr.val, typeCast.symbol);
    }
    typeCast.val = typeCast.expr.val;
  }
}
