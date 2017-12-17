//### This file created by BYACC 1.8(/Java extension  1.13)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//###           14 Sep 06  -- Keltin Leung-- ReduceListener support, eliminate underflow report in error recovery
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 11 "Parser.y"
package decaf.frontend;

import decaf.tree.Tree;
import decaf.tree.Tree.*;
import decaf.error.*;
import java.util.*;
//#line 25 "Parser.java"
interface ReduceListener {
  public boolean onReduce(String rule);
}




public class Parser
             extends BaseParser
             implements ReduceListener
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

ReduceListener reduceListener = null;
void yyclearin ()       {yychar = (-1);}
void yyerrok ()         {yyerrflag=0;}
void addReduceListener(ReduceListener l) {
  reduceListener = l;}


//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:SemValue
String   yytext;//user variable to return contextual strings
SemValue yyval; //used to return semantic vals from action routines
SemValue yylval;//the 'lval' (result) I got from yylex()
SemValue valstk[] = new SemValue[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new SemValue();
  yylval=new SemValue();
  valptr=-1;
}
final void val_push(SemValue val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    SemValue[] newstack = new SemValue[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final SemValue val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final SemValue val_peek(int relative)
{
  return valstk[valptr-relative];
}
//#### end semantic value section ####
public final static short VOID=257;
public final static short BOOL=258;
public final static short INT=259;
public final static short STRING=260;
public final static short CLASS=261;
public final static short NULL=262;
public final static short EXTENDS=263;
public final static short THIS=264;
public final static short WHILE=265;
public final static short FOR=266;
public final static short IF=267;
public final static short ELSE=268;
public final static short RETURN=269;
public final static short BREAK=270;
public final static short NEW=271;
public final static short PRINT=272;
public final static short READ_INTEGER=273;
public final static short READ_LINE=274;
public final static short LITERAL=275;
public final static short IDENTIFIER=276;
public final static short AND=277;
public final static short OR=278;
public final static short STATIC=279;
public final static short INSTANCEOF=280;
public final static short LESS_EQUAL=281;
public final static short GREATER_EQUAL=282;
public final static short EQUAL=283;
public final static short NOT_EQUAL=284;
public final static short COMPLEX=285;
public final static short PRINTCOMP=286;
public final static short CASE=287;
public final static short DEFAULT=288;
public final static short SUPER=289;
public final static short DCOPY=290;
public final static short SCOPY=291;
public final static short DO=292;
public final static short OD=293;
public final static short DOSEPARATOR=294;
public final static short UMINUS=295;
public final static short EMPTY=296;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    5,    5,    5,    5,    5,
    5,    5,    2,    6,    6,    7,    7,    7,    9,    9,
   10,   10,    8,    8,   11,   12,   12,   13,   13,   13,
   13,   13,   13,   13,   13,   13,   13,   13,   14,   14,
   14,   26,   26,   23,   23,   25,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   28,
   28,   27,   27,   30,   30,   16,   17,   29,   31,   31,
   33,   32,   21,   15,   34,   34,   18,   18,   19,   20,
   22,   35,   35,   36,
};
final static short yylen[] = {                            2,
    1,    2,    1,    2,    2,    1,    1,    1,    1,    1,
    2,    3,    6,    2,    0,    2,    2,    0,    1,    0,
    3,    1,    7,    6,    3,    2,    0,    1,    2,    1,
    1,    1,    2,    2,    2,    2,    2,    1,    3,    1,
    0,    2,    0,    2,    4,    5,    1,    1,    1,    1,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    3,    2,    2,    2,    2,    2,    3,
    3,    1,    1,    4,    5,    6,    5,    4,    4,    1,
    1,    1,    0,    3,    1,    5,    9,    8,    2,    0,
    4,    4,    1,    6,    2,    0,    2,    1,    4,    4,
    3,    3,    1,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    2,    0,    0,   14,   18,
    0,    7,    8,    6,    9,    0,    0,   13,   10,   16,
    0,    0,   17,   11,    0,    4,    0,    0,    0,    0,
   12,    0,   22,    0,    0,    0,    0,    5,    0,    0,
    0,   27,   24,   21,   23,    0,   81,   72,    0,    0,
    0,    0,   93,    0,    0,    0,    0,   80,    0,    0,
    0,    0,   25,    0,    0,    0,    0,    0,   73,    0,
    0,    0,   28,   38,   26,    0,   30,   31,   32,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   49,   50,
    0,    0,    0,   47,    0,   48,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  103,   29,   33,   34,   35,
   36,   37,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   42,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   70,   71,    0,    0,
   64,    0,    0,    0,    0,    0,  101,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   74,    0,    0,
   99,    0,    0,  100,    0,   78,   79,  104,  102,   45,
    0,    0,   86,    0,    0,   75,    0,    0,   77,   90,
   46,    0,    0,   94,   76,    0,    0,   95,    0,    0,
    0,   89,    0,    0,    0,   88,   87,    0,    0,   92,
   91,
};
final static short yydgoto[] = {                          2,
    3,    4,   73,   21,   34,    8,   11,   23,   35,   36,
   74,   46,   75,   76,   77,   78,   79,   80,   81,   82,
   83,   84,   94,   86,   96,   88,  191,   89,   90,  146,
  206,  211,  212,  204,  115,  116,
};
final static short yysindex[] = {                      -239,
 -249,    0, -239,    0, -231,    0, -233,  -72,    0,    0,
  192,    0,    0,    0,    0, -221,  316,    0,    0,    0,
    9,  -85,    0,    0,  -83,    0,   22,   -6,   45,  316,
    0,  316,    0,  -74,   52,   55,   59,    0,  -22,  316,
  -22,    0,    0,    0,    0,    5,    0,    0,   63,   64,
   67,  103,    0,  502,   70,   71,   72,    0,   75,  103,
  103,   73,    0,  103,  103,  103,   79,   80,    0,   82,
   83,  103,    0,    0,    0,   65,    0,    0,    0,   66,
   68,   76,   85,   87,   86, 1005,    0, -145,    0,    0,
  103,  103,  103,    0, 1005,    0,   92,   43,  103,   99,
  100,  103,  -26,  -26, -134,  552,  -26,  -26,  -26,  103,
  103,  103,  103,  563, -197,    0,    0,    0,    0,    0,
    0,    0,  103,  103,  103,  103,  103,  103,  103,  103,
  103,  103,  103,  103,  103,  103,    0,  103,  109,  614,
   91,  626,  111,   81, 1005,   23,    0,    0,  652,  112,
    0,   34,  751,  777,  811,   41,    0,  103, 1005, 1072,
 1061,   11,   11,  -32,  -32,   42,   42,  -26,  -26,  -26,
   11,   11,  877,  103,   41,  103,   41,    0,  901,  103,
    0, -121,  103,    0,   33,    0,    0,    0,    0,    0,
  116,  114,    0,  912, -108,    0, 1005,  120,    0,    0,
    0,  103,   41,    0,    0, -228,  122,    0,  107,  108,
   46,    0,   41,  103,  103,    0,    0,  936,  962,    0,
    0,
};
final static short yyrindex[] = {                         0,
    0,    0,  170,    0,   49,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  117,    0,    0,  132,
    0,  132,    0,    0,    0,  134,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -58,    0,    0,    0,    0,
    0,  -55,    0,    0,    0,    0,    0,    0,    0,  -99,
  -99,  -99,    0,  -99,  -99,  -99,    0,    0,    0,    0,
    0,  -99,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1037,    0,  525,    0,    0,    0,
  -99,  -58,  -99,    0,  123,    0,    0,    0,  -99,    0,
    0,  -99,  354,  363,    0,    0,  401,  425,  454,  -99,
  -99,  -99,  -99,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -99,  -99,  -99,  -99,  -99,  -99,  -99,  -99,
  -99,  -99,  -99,  -99,  -99,  -99,    0,  -99,  153,    0,
    0,    0,    0,  -99,   39,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -58,    0,  -99,  -17,  110,
  -23,  707,  979,  698,  711, 1081, 1106,  463,  490,  499,
 1027, 1128,    0,  -25,  -58,  -99,  -58,    0,    0,  -99,
    0,    0,  -99,    0,    0,    0,    0,    0,    0,    0,
    0,  138,    0,    0,  -33,    0,   50,    0,    0,    0,
    0,   -8,  -58,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -58,  -99,  -99,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
    0,  181,  174,   31,   12,    0,    0,    0,  154,    0,
   -2,    0, -131,  -73,    0,    0,    0,    0,    0,    0,
    0,    0,    3, 1308,    6,    0,    0,  -19,    0, -101,
    0,    0,    0,    0,    0,   30,
};
final static int YYTABLESIZE=1523;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         96,
   41,   96,   96,   98,  134,   28,   96,   28,  152,  132,
  130,   96,  131,  137,  133,   83,   28,   63,  141,  137,
   63,    1,   22,   39,  188,   96,    5,  136,   25,  135,
   96,    7,   41,   47,   63,   63,   43,   61,   45,   66,
   65,   39,    9,  193,   62,  195,   58,  134,   85,   60,
   10,   87,  132,  130,   24,  131,  137,  133,  138,  209,
   33,   30,   33,  181,  138,   98,  180,   26,   64,   63,
   44,  208,  192,   61,  184,   66,   65,  180,  134,   85,
   62,  217,   85,  132,   32,   60,   31,  137,  133,   96,
   84,   96,   39,   84,   85,  157,  158,   87,   40,   41,
   42,  138,   91,   92,   64,   61,   93,   66,   65,   99,
  100,  101,   62,   61,  102,   66,   65,   60,  110,  111,
   62,  112,  113,  117,  118,   60,  119,   42,  207,   63,
  139,  143,  138,  144,  120,   61,   64,   66,   65,  147,
  148,  150,   62,  121,   64,  122,  123,   60,  174,  176,
   62,  178,  183,   62,  198,  200,  201,  180,   85,  203,
  205,   87,  213,   42,  214,  215,   64,   62,   62,    1,
  216,   15,   20,   31,   19,    5,   43,   85,   82,   85,
   87,   97,   87,    6,   20,   37,  210,  189,    0,   44,
   27,    0,   29,   44,   44,   44,   44,   44,   44,   44,
    0,   38,   62,    0,   85,   85,    0,   87,   87,    0,
   44,   44,   44,   44,   44,   85,    0,   43,   87,    0,
   43,    0,    0,   96,   96,   96,   96,   96,   96,    0,
   96,   96,   96,   96,    0,   96,   96,   96,   96,   96,
   96,   96,   96,   44,    0,   44,   96,    0,  126,  127,
   43,   96,   96,   96,   63,   96,   96,   96,   96,   96,
   96,   12,   13,   14,   15,   16,   47,   43,   48,   49,
   50,   51,    0,   52,   53,   54,   55,   56,   57,   58,
    0,    0,    0,    0,   59,    0,    0,    0,    0,   19,
   67,   68,    0,   69,   70,   71,   72,   12,   13,   14,
   15,   16,   47,    0,   48,   49,   50,   51,    0,   52,
   53,   54,   55,   56,   57,   58,   18,    0,    0,    0,
   59,    0,    0,    0,    0,   19,   67,   68,    0,   69,
   70,   71,   72,  105,   47,    0,   48,    0,    0,    0,
    0,    0,   47,   54,   48,   56,   57,   58,    0,    0,
    0,   54,   59,   56,   57,   58,    0,    0,    0,   68,
   59,   69,   70,   71,   47,    0,   48,   68,    0,   69,
   70,   71,    0,   54,    0,   56,   57,   58,    0,    0,
    0,    0,   59,    0,    0,    0,   62,   62,    0,   68,
   65,   69,   70,   71,   65,   65,   65,   65,   65,   66,
   65,    0,    0,   66,   66,   66,   66,   66,    0,   66,
    0,   65,   65,   65,    0,   65,    0,    0,    0,    0,
   66,   66,   66,    0,   66,    0,    0,    0,    0,   44,
   44,    0,    0,   44,   44,   44,   44,   67,    0,    0,
    0,   67,   67,   67,   67,   67,   65,   67,   12,   13,
   14,   15,   16,    0,    0,   66,    0,    0,   67,   67,
   67,   68,   67,    0,    0,   68,   68,   68,   68,   68,
   17,   68,    0,    0,    0,    0,   19,    0,    0,    0,
    0,    0,   68,   68,   68,    0,   68,    0,    0,    0,
   69,    0,    0,   67,   69,   69,   69,   69,   69,   53,
   69,    0,    0,   53,   53,   53,   53,   53,    0,   53,
    0,   69,   69,   69,    0,   69,    0,   68,    0,    0,
   53,   53,   53,    0,   53,    0,   54,    0,    0,    0,
   54,   54,   54,   54,   54,   55,   54,    0,    0,   55,
   55,   55,   55,   55,    0,   55,   69,   54,   54,   54,
    0,   54,    0,    0,    0,   53,   55,   55,   55,    0,
   55,   48,    0,    0,    0,   40,   48,   48,    0,   48,
   48,   48,   12,   13,   14,   15,   16,    0,    0,    0,
    0,    0,   54,   40,   48,    0,   48,    0,  134,    0,
    0,   55,  151,  132,  130,    0,  131,  137,  133,  134,
   19,    0,    0,    0,  132,  130,    0,  131,  137,  133,
    0,  136,    0,  135,    0,   48,    0,    0,    0,    0,
  156,    0,  136,    0,  135,    0,    0,    0,    0,    0,
   65,   65,    0,    0,   65,   65,   65,   65,    0,   66,
   66,    0,  138,   66,   66,   66,   66,    0,    0,    0,
  134,    0,    0,  138,  175,  132,  130,    0,  131,  137,
  133,    0,  134,    0,    0,    0,  177,  132,  130,    0,
  131,  137,  133,  136,    0,  135,    0,   67,   67,    0,
    0,   67,   67,   67,   67,  136,    0,  135,  134,    0,
    0,    0,    0,  132,  130,  182,  131,  137,  133,    0,
    0,   68,   68,    0,  138,   68,   68,   68,   68,    0,
    0,  136,    0,  135,    0,    0,  138,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   69,   69,    0,    0,   69,   69,   69,   69,   56,   53,
   53,   56,  138,   53,   53,   53,   53,   60,    0,    0,
   60,   57,    0,    0,   57,   56,   56,    0,   12,   13,
   14,   15,   16,    0,   60,   60,   54,   54,   57,   57,
   54,   54,   54,   54,    0,   55,   55,   97,    0,   55,
   55,   55,   55,    0,    0,    0,   19,  134,    0,    0,
   56,  185,  132,  130,    0,  131,  137,  133,    0,   60,
    0,   48,   48,   57,    0,   48,   48,   48,   48,    0,
  136,    0,  135,  134,    0,    0,    0,  186,  132,  130,
    0,  131,  137,  133,    0,    0,    0,    0,  124,  125,
    0,    0,  126,  127,  128,  129,  136,    0,  135,  124,
  125,  138,    0,  126,  127,  128,  129,  134,    0,    0,
    0,  187,  132,  130,    0,  131,  137,  133,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  138,    0,    0,
  136,    0,  135,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  124,  125,    0,    0,  126,  127,  128,  129,    0,    0,
    0,  138,  124,  125,    0,    0,  126,  127,  128,  129,
    0,    0,    0,  134,    0,    0,    0,    0,  132,  130,
    0,  131,  137,  133,    0,    0,    0,    0,  124,  125,
    0,    0,  126,  127,  128,  129,  136,  134,  135,    0,
    0,    0,  132,  130,    0,  131,  137,  133,  134,    0,
    0,    0,    0,  132,  130,    0,  131,  137,  133,    0,
  136,    0,  135,    0,    0,    0,    0,  138,    0,  190,
  202,  136,  134,  135,   56,   56,    0,  132,  130,    0,
  131,  137,  133,   60,   60,    0,    0,   57,   57,   60,
   60,  138,    0,  196,  220,  136,    0,  135,  134,    0,
    0,    0,  138,  132,  130,    0,  131,  137,  133,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   61,
  221,  136,   61,  135,    0,    0,  138,  124,  125,    0,
    0,  126,  127,  128,  129,    0,   61,   61,    0,    0,
    0,  134,    0,    0,    0,    0,  132,  130,    0,  131,
  137,  133,  138,  124,  125,    0,    0,  126,  127,  128,
  129,    0,    0,    0,  136,    0,  135,   59,    0,    0,
   59,   61,    0,   47,    0,    0,    0,    0,   47,   47,
    0,   47,   47,   47,   59,   59,    0,  124,  125,    0,
    0,  126,  127,  128,  129,  138,   47,  134,   47,    0,
    0,    0,  132,  130,    0,  131,  137,  133,  134,    0,
    0,    0,    0,  132,  130,    0,  131,  137,  133,   59,
  136,   51,  135,   51,   51,   51,    0,   47,    0,    0,
    0,  136,    0,  135,    0,    0,    0,    0,   51,   51,
   51,    0,   51,    0,    0,    0,   52,    0,   52,   52,
   52,  138,    0,  124,  125,    0,    0,  126,  127,  128,
  129,    0,  138,   52,   52,   52,    0,   52,   58,    0,
    0,   58,    0,   51,    0,    0,    0,  124,  125,    0,
    0,  126,  127,  128,  129,   58,   58,    0,  124,  125,
    0,    0,  126,  127,  128,  129,    0,    0,   52,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  124,  125,    0,    0,  126,  127,  128,  129,
   58,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  124,  125,
    0,    0,  126,  127,  128,  129,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   61,   61,    0,    0,    0,
    0,   61,   61,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  124,  125,    0,    0,  126,  127,  128,  129,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   59,   59,    0,    0,    0,    0,   59,
   59,    0,    0,   47,   47,    0,    0,   47,   47,   47,
   47,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  124,    0,    0,
    0,  126,  127,  128,  129,    0,    0,    0,    0,    0,
    0,    0,  126,  127,  128,  129,    0,   51,   51,   95,
    0,   51,   51,   51,   51,    0,    0,  103,  104,  106,
    0,  107,  108,  109,    0,    0,    0,    0,    0,  114,
    0,    0,   52,   52,    0,    0,   52,   52,   52,   52,
    0,    0,    0,    0,    0,    0,    0,    0,  140,    0,
  142,    0,    0,    0,   58,   58,  145,    0,    0,  149,
   58,   58,    0,    0,    0,    0,    0,  145,  153,  154,
  155,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  159,  160,  161,  162,  163,  164,  165,  166,  167,  168,
  169,  170,  171,  172,    0,  173,    0,    0,    0,    0,
    0,  179,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  114,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  145,    0,  194,    0,    0,    0,  197,    0,    0,
  199,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  218,  219,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
   59,   35,   36,   59,   37,   91,   40,   91,  110,   42,
   43,   45,   45,   46,   47,   41,   91,   41,   92,   46,
   44,  261,   11,   41,  156,   59,  276,   60,   17,   62,
   64,  263,   41,  262,   58,   59,   39,   33,   41,   35,
   36,   59,  276,  175,   40,  177,  275,   37,   46,   45,
  123,   46,   42,   43,  276,   45,   46,   47,   91,  288,
   30,   40,   32,   41,   91,   54,   44,   59,   64,   93,
   40,  203,  174,   33,   41,   35,   36,   44,   37,   41,
   40,  213,   44,   42,   40,   45,   93,   46,   47,  123,
   41,  125,   41,   44,   92,  293,  294,   92,   44,   41,
  123,   91,   40,   40,   64,   33,   40,   35,   36,   40,
   40,   40,   40,   33,   40,   35,   36,   45,   40,   40,
   40,   40,   40,   59,   59,   45,   59,  123,  202,  125,
  276,   40,   91,   91,   59,   33,   64,   35,   36,   41,
   41,  276,   40,   59,   64,   59,   61,   45,   40,   59,
   41,   41,   41,   44,  276,  123,   41,   44,  156,  268,
   41,  156,   41,  123,   58,   58,   64,   58,   59,    0,
  125,  123,   41,   93,   41,   59,  276,  175,   41,  177,
  175,   59,  177,    3,   11,   32,  206,  158,   -1,   37,
  276,   -1,  276,   41,   42,   43,   44,   45,   46,   47,
   -1,  276,   93,   -1,  202,  203,   -1,  202,  203,   -1,
   58,   59,   60,   61,   62,  213,   -1,  276,  213,   -1,
  276,   -1,   -1,  257,  258,  259,  260,  261,  262,   -1,
  264,  265,  266,  267,   -1,  269,  270,  271,  272,  273,
  274,  275,  276,   91,   -1,   93,  280,   -1,  281,  282,
  276,  285,  286,  287,  278,  289,  290,  291,  292,  293,
  294,  257,  258,  259,  260,  261,  262,  276,  264,  265,
  266,  267,   -1,  269,  270,  271,  272,  273,  274,  275,
   -1,   -1,   -1,   -1,  280,   -1,   -1,   -1,   -1,  285,
  286,  287,   -1,  289,  290,  291,  292,  257,  258,  259,
  260,  261,  262,   -1,  264,  265,  266,  267,   -1,  269,
  270,  271,  272,  273,  274,  275,  125,   -1,   -1,   -1,
  280,   -1,   -1,   -1,   -1,  285,  286,  287,   -1,  289,
  290,  291,  292,  261,  262,   -1,  264,   -1,   -1,   -1,
   -1,   -1,  262,  271,  264,  273,  274,  275,   -1,   -1,
   -1,  271,  280,  273,  274,  275,   -1,   -1,   -1,  287,
  280,  289,  290,  291,  262,   -1,  264,  287,   -1,  289,
  290,  291,   -1,  271,   -1,  273,  274,  275,   -1,   -1,
   -1,   -1,  280,   -1,   -1,   -1,  277,  278,   -1,  287,
   37,  289,  290,  291,   41,   42,   43,   44,   45,   37,
   47,   -1,   -1,   41,   42,   43,   44,   45,   -1,   47,
   -1,   58,   59,   60,   -1,   62,   -1,   -1,   -1,   -1,
   58,   59,   60,   -1,   62,   -1,   -1,   -1,   -1,  277,
  278,   -1,   -1,  281,  282,  283,  284,   37,   -1,   -1,
   -1,   41,   42,   43,   44,   45,   93,   47,  257,  258,
  259,  260,  261,   -1,   -1,   93,   -1,   -1,   58,   59,
   60,   37,   62,   -1,   -1,   41,   42,   43,   44,   45,
  279,   47,   -1,   -1,   -1,   -1,  285,   -1,   -1,   -1,
   -1,   -1,   58,   59,   60,   -1,   62,   -1,   -1,   -1,
   37,   -1,   -1,   93,   41,   42,   43,   44,   45,   37,
   47,   -1,   -1,   41,   42,   43,   44,   45,   -1,   47,
   -1,   58,   59,   60,   -1,   62,   -1,   93,   -1,   -1,
   58,   59,   60,   -1,   62,   -1,   37,   -1,   -1,   -1,
   41,   42,   43,   44,   45,   37,   47,   -1,   -1,   41,
   42,   43,   44,   45,   -1,   47,   93,   58,   59,   60,
   -1,   62,   -1,   -1,   -1,   93,   58,   59,   60,   -1,
   62,   37,   -1,   -1,   -1,   41,   42,   43,   -1,   45,
   46,   47,  257,  258,  259,  260,  261,   -1,   -1,   -1,
   -1,   -1,   93,   59,   60,   -1,   62,   -1,   37,   -1,
   -1,   93,   41,   42,   43,   -1,   45,   46,   47,   37,
  285,   -1,   -1,   -1,   42,   43,   -1,   45,   46,   47,
   -1,   60,   -1,   62,   -1,   91,   -1,   -1,   -1,   -1,
   58,   -1,   60,   -1,   62,   -1,   -1,   -1,   -1,   -1,
  277,  278,   -1,   -1,  281,  282,  283,  284,   -1,  277,
  278,   -1,   91,  281,  282,  283,  284,   -1,   -1,   -1,
   37,   -1,   -1,   91,   41,   42,   43,   -1,   45,   46,
   47,   -1,   37,   -1,   -1,   -1,   41,   42,   43,   -1,
   45,   46,   47,   60,   -1,   62,   -1,  277,  278,   -1,
   -1,  281,  282,  283,  284,   60,   -1,   62,   37,   -1,
   -1,   -1,   -1,   42,   43,   44,   45,   46,   47,   -1,
   -1,  277,  278,   -1,   91,  281,  282,  283,  284,   -1,
   -1,   60,   -1,   62,   -1,   -1,   91,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  277,  278,   -1,   -1,  281,  282,  283,  284,   41,  277,
  278,   44,   91,  281,  282,  283,  284,   41,   -1,   -1,
   44,   41,   -1,   -1,   44,   58,   59,   -1,  257,  258,
  259,  260,  261,   -1,   58,   59,  277,  278,   58,   59,
  281,  282,  283,  284,   -1,  277,  278,  276,   -1,  281,
  282,  283,  284,   -1,   -1,   -1,  285,   37,   -1,   -1,
   93,   41,   42,   43,   -1,   45,   46,   47,   -1,   93,
   -1,  277,  278,   93,   -1,  281,  282,  283,  284,   -1,
   60,   -1,   62,   37,   -1,   -1,   -1,   41,   42,   43,
   -1,   45,   46,   47,   -1,   -1,   -1,   -1,  277,  278,
   -1,   -1,  281,  282,  283,  284,   60,   -1,   62,  277,
  278,   91,   -1,  281,  282,  283,  284,   37,   -1,   -1,
   -1,   41,   42,   43,   -1,   45,   46,   47,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   91,   -1,   -1,
   60,   -1,   62,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  277,  278,   -1,   -1,  281,  282,  283,  284,   -1,   -1,
   -1,   91,  277,  278,   -1,   -1,  281,  282,  283,  284,
   -1,   -1,   -1,   37,   -1,   -1,   -1,   -1,   42,   43,
   -1,   45,   46,   47,   -1,   -1,   -1,   -1,  277,  278,
   -1,   -1,  281,  282,  283,  284,   60,   37,   62,   -1,
   -1,   -1,   42,   43,   -1,   45,   46,   47,   37,   -1,
   -1,   -1,   -1,   42,   43,   -1,   45,   46,   47,   -1,
   60,   -1,   62,   -1,   -1,   -1,   -1,   91,   -1,   93,
   59,   60,   37,   62,  277,  278,   -1,   42,   43,   -1,
   45,   46,   47,  277,  278,   -1,   -1,  277,  278,  283,
  284,   91,   -1,   93,   59,   60,   -1,   62,   37,   -1,
   -1,   -1,   91,   42,   43,   -1,   45,   46,   47,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   41,
   59,   60,   44,   62,   -1,   -1,   91,  277,  278,   -1,
   -1,  281,  282,  283,  284,   -1,   58,   59,   -1,   -1,
   -1,   37,   -1,   -1,   -1,   -1,   42,   43,   -1,   45,
   46,   47,   91,  277,  278,   -1,   -1,  281,  282,  283,
  284,   -1,   -1,   -1,   60,   -1,   62,   41,   -1,   -1,
   44,   93,   -1,   37,   -1,   -1,   -1,   -1,   42,   43,
   -1,   45,   46,   47,   58,   59,   -1,  277,  278,   -1,
   -1,  281,  282,  283,  284,   91,   60,   37,   62,   -1,
   -1,   -1,   42,   43,   -1,   45,   46,   47,   37,   -1,
   -1,   -1,   -1,   42,   43,   -1,   45,   46,   47,   93,
   60,   41,   62,   43,   44,   45,   -1,   91,   -1,   -1,
   -1,   60,   -1,   62,   -1,   -1,   -1,   -1,   58,   59,
   60,   -1,   62,   -1,   -1,   -1,   41,   -1,   43,   44,
   45,   91,   -1,  277,  278,   -1,   -1,  281,  282,  283,
  284,   -1,   91,   58,   59,   60,   -1,   62,   41,   -1,
   -1,   44,   -1,   93,   -1,   -1,   -1,  277,  278,   -1,
   -1,  281,  282,  283,  284,   58,   59,   -1,  277,  278,
   -1,   -1,  281,  282,  283,  284,   -1,   -1,   93,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  277,  278,   -1,   -1,  281,  282,  283,  284,
   93,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  277,  278,
   -1,   -1,  281,  282,  283,  284,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  277,  278,   -1,   -1,   -1,
   -1,  283,  284,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  277,  278,   -1,   -1,  281,  282,  283,  284,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  277,  278,   -1,   -1,   -1,   -1,  283,
  284,   -1,   -1,  277,  278,   -1,   -1,  281,  282,  283,
  284,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  277,   -1,   -1,
   -1,  281,  282,  283,  284,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  281,  282,  283,  284,   -1,  277,  278,   52,
   -1,  281,  282,  283,  284,   -1,   -1,   60,   61,   62,
   -1,   64,   65,   66,   -1,   -1,   -1,   -1,   -1,   72,
   -1,   -1,  277,  278,   -1,   -1,  281,  282,  283,  284,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   91,   -1,
   93,   -1,   -1,   -1,  277,  278,   99,   -1,   -1,  102,
  283,  284,   -1,   -1,   -1,   -1,   -1,  110,  111,  112,
  113,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  123,  124,  125,  126,  127,  128,  129,  130,  131,  132,
  133,  134,  135,  136,   -1,  138,   -1,   -1,   -1,   -1,
   -1,  144,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  158,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  174,   -1,  176,   -1,   -1,   -1,  180,   -1,   -1,
  183,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  214,  215,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=296;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,"'#'","'$'","'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,"'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"VOID","BOOL","INT","STRING",
"CLASS","NULL","EXTENDS","THIS","WHILE","FOR","IF","ELSE","RETURN","BREAK",
"NEW","PRINT","READ_INTEGER","READ_LINE","LITERAL","IDENTIFIER","AND","OR",
"STATIC","INSTANCEOF","LESS_EQUAL","GREATER_EQUAL","EQUAL","NOT_EQUAL",
"COMPLEX","PRINTCOMP","CASE","DEFAULT","SUPER","DCOPY","SCOPY","DO","OD",
"DOSEPARATOR","UMINUS","EMPTY",
};
final static String yyrule[] = {
"$accept : Program",
"Program : ClassList",
"ClassList : ClassList ClassDef",
"ClassList : ClassDef",
"VariableDef : Variable ';'",
"Variable : Type IDENTIFIER",
"Type : INT",
"Type : VOID",
"Type : BOOL",
"Type : STRING",
"Type : COMPLEX",
"Type : CLASS IDENTIFIER",
"Type : Type '[' ']'",
"ClassDef : CLASS IDENTIFIER ExtendsClause '{' FieldList '}'",
"ExtendsClause : EXTENDS IDENTIFIER",
"ExtendsClause :",
"FieldList : FieldList VariableDef",
"FieldList : FieldList FunctionDef",
"FieldList :",
"Formals : VariableList",
"Formals :",
"VariableList : VariableList ',' Variable",
"VariableList : Variable",
"FunctionDef : STATIC Type IDENTIFIER '(' Formals ')' StmtBlock",
"FunctionDef : Type IDENTIFIER '(' Formals ')' StmtBlock",
"StmtBlock : '{' StmtList '}'",
"StmtList : StmtList Stmt",
"StmtList :",
"Stmt : VariableDef",
"Stmt : SimpleStmt ';'",
"Stmt : IfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"Stmt : ReturnStmt ';'",
"Stmt : PrintStmt ';'",
"Stmt : PrintCompStmt ';'",
"Stmt : BreakStmt ';'",
"Stmt : DoStmt ';'",
"Stmt : StmtBlock",
"SimpleStmt : LValue '=' Expr",
"SimpleStmt : Call",
"SimpleStmt :",
"Receiver : Expr '.'",
"Receiver :",
"LValue : Receiver IDENTIFIER",
"LValue : Expr '[' Expr ']'",
"Call : Receiver IDENTIFIER '(' Actuals ')'",
"Expr : LValue",
"Expr : Call",
"Expr : Constant",
"Expr : Case",
"Expr : Expr '+' Expr",
"Expr : Expr '-' Expr",
"Expr : Expr '*' Expr",
"Expr : Expr '/' Expr",
"Expr : Expr '%' Expr",
"Expr : Expr EQUAL Expr",
"Expr : Expr NOT_EQUAL Expr",
"Expr : Expr '<' Expr",
"Expr : Expr '>' Expr",
"Expr : Expr LESS_EQUAL Expr",
"Expr : Expr GREATER_EQUAL Expr",
"Expr : Expr AND Expr",
"Expr : Expr OR Expr",
"Expr : '(' Expr ')'",
"Expr : '-' Expr",
"Expr : '!' Expr",
"Expr : '@' Expr",
"Expr : '$' Expr",
"Expr : '#' Expr",
"Expr : READ_INTEGER '(' ')'",
"Expr : READ_LINE '(' ')'",
"Expr : THIS",
"Expr : SUPER",
"Expr : NEW IDENTIFIER '(' ')'",
"Expr : NEW Type '[' Expr ']'",
"Expr : INSTANCEOF '(' Expr ',' IDENTIFIER ')'",
"Expr : '(' CLASS IDENTIFIER ')' Expr",
"Expr : DCOPY '(' Expr ')'",
"Expr : SCOPY '(' Expr ')'",
"Constant : LITERAL",
"Constant : NULL",
"Actuals : ExprList",
"Actuals :",
"ExprList : ExprList ',' Expr",
"ExprList : Expr",
"WhileStmt : WHILE '(' Expr ')' Stmt",
"ForStmt : FOR '(' SimpleStmt ';' Expr ';' SimpleStmt ')' Stmt",
"Case : CASE '(' Expr ')' '{' CasesList DefaultItem '}'",
"CasesList : CasesList CaseItem",
"CasesList :",
"CaseItem : Constant ':' Expr ';'",
"DefaultItem : DEFAULT ':' Expr ';'",
"BreakStmt : BREAK",
"IfStmt : IF '(' Expr ')' Stmt ElseClause",
"ElseClause : ELSE Stmt",
"ElseClause :",
"ReturnStmt : RETURN Expr",
"ReturnStmt : RETURN",
"PrintStmt : PRINT '(' ExprList ')'",
"PrintCompStmt : PRINTCOMP '(' ExprList ')'",
"DoStmt : DO DoBranch OD",
"DoBranch : DoBranch DOSEPARATOR DoSubStmt",
"DoBranch : DoSubStmt",
"DoSubStmt : Expr ':' Stmt",
};

//#line 512 "Parser.y"
    
	/**
	 * 打印当前归约所用的语法规则<br>
	 * 请勿修改。
	 */
    public boolean onReduce(String rule) {
		if (rule.startsWith("$$"))
			return false;
		else
			rule = rule.replaceAll(" \\$\\$\\d+", "");

   	    if (rule.endsWith(":"))
    	    System.out.println(rule + " <empty>");
   	    else
			System.out.println(rule);
		return false;
    }
    
    public void diagnose() {
		addReduceListener(this);
		yyparse();
	}
//#line 734 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    //if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      //if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        //if (yychar < 0)    //it it didn't work/error
        //  {
        //  yychar = 0;      //change it to default string (no -1!)
          //if (yydebug)
          //  yylexdebug(yystate,yychar);
        //  }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        //if (yydebug)
          //debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      //if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0 || valptr<0)   //check for under & overflow here
            {
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            //if (yydebug)
              //debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            //if (yydebug)
              //debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0 || valptr<0)   //check for under & overflow here
              {
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        //if (yydebug)
          //{
          //yys = null;
          //if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          //if (yys == null) yys = "illegal-symbol";
          //debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          //}
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    //if (yydebug)
      //debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    if (reduceListener == null || reduceListener.onReduce(yyrule[yyn])) // if intercepted!
      switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 57 "Parser.y"
{
						tree = new Tree.TopLevel(val_peek(0).clist, val_peek(0).loc);
					}
break;
case 2:
//#line 63 "Parser.y"
{
						yyval.clist.add(val_peek(0).cdef);
					}
break;
case 3:
//#line 67 "Parser.y"
{
                		yyval.clist = new ArrayList<Tree.ClassDef>();
                		yyval.clist.add(val_peek(0).cdef);
                	}
break;
case 5:
//#line 77 "Parser.y"
{
						yyval.vdef = new Tree.VarDef(val_peek(0).ident, val_peek(1).type, val_peek(0).loc);
					}
break;
case 6:
//#line 83 "Parser.y"
{
						yyval.type = new Tree.TypeIdent(Tree.INT, val_peek(0).loc);
					}
break;
case 7:
//#line 87 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.VOID, val_peek(0).loc);
                	}
break;
case 8:
//#line 91 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.BOOL, val_peek(0).loc);
                	}
break;
case 9:
//#line 95 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.STRING, val_peek(0).loc);
                	}
break;
case 10:
//#line 100 "Parser.y"
{
						yyval.type = new Tree.TypeIdent(Tree.COMPLEX, val_peek(0).loc);
					}
break;
case 11:
//#line 104 "Parser.y"
{
                		yyval.type = new Tree.TypeClass(val_peek(0).ident, val_peek(1).loc);
                	}
break;
case 12:
//#line 108 "Parser.y"
{
                		yyval.type = new Tree.TypeArray(val_peek(2).type, val_peek(2).loc);
                	}
break;
case 13:
//#line 114 "Parser.y"
{
						yyval.cdef = new Tree.ClassDef(val_peek(4).ident, val_peek(3).ident, val_peek(1).flist, val_peek(5).loc);
					}
break;
case 14:
//#line 120 "Parser.y"
{
						yyval.ident = val_peek(0).ident;
					}
break;
case 15:
//#line 124 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 16:
//#line 130 "Parser.y"
{
						yyval.flist.add(val_peek(0).vdef);
					}
break;
case 17:
//#line 134 "Parser.y"
{
						yyval.flist.add(val_peek(0).fdef);
					}
break;
case 18:
//#line 138 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.flist = new ArrayList<Tree>();
                	}
break;
case 20:
//#line 146 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.vlist = new ArrayList<Tree.VarDef>(); 
                	}
break;
case 21:
//#line 153 "Parser.y"
{
						yyval.vlist.add(val_peek(0).vdef);
					}
break;
case 22:
//#line 157 "Parser.y"
{
                		yyval.vlist = new ArrayList<Tree.VarDef>();
						yyval.vlist.add(val_peek(0).vdef);
                	}
break;
case 23:
//#line 164 "Parser.y"
{
						yyval.fdef = new MethodDef(true, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 24:
//#line 168 "Parser.y"
{
						yyval.fdef = new MethodDef(false, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 25:
//#line 174 "Parser.y"
{
						yyval.stmt = new Block(val_peek(1).slist, val_peek(2).loc);
					}
break;
case 26:
//#line 180 "Parser.y"
{
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 27:
//#line 184 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.slist = new ArrayList<Tree>();
                	}
break;
case 28:
//#line 191 "Parser.y"
{
						yyval.stmt = val_peek(0).vdef;
					}
break;
case 29:
//#line 196 "Parser.y"
{
                		if (yyval.stmt == null) {
                			yyval.stmt = new Tree.Skip(val_peek(0).loc);
                		}
                	}
break;
case 39:
//#line 213 "Parser.y"
{
						yyval.stmt = new Tree.Assign(val_peek(2).lvalue, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 40:
//#line 217 "Parser.y"
{
                		yyval.stmt = new Tree.Exec(val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 41:
//#line 221 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 43:
//#line 228 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 44:
//#line 234 "Parser.y"
{
						yyval.lvalue = new Tree.Ident(val_peek(1).expr, val_peek(0).ident, val_peek(0).loc);
						if (val_peek(1).loc == null) {
							yyval.loc = val_peek(0).loc;
						}
					}
break;
case 45:
//#line 241 "Parser.y"
{
                		yyval.lvalue = new Tree.Indexed(val_peek(3).expr, val_peek(1).expr, val_peek(3).loc);
                	}
break;
case 46:
//#line 247 "Parser.y"
{
						yyval.expr = new Tree.CallExpr(val_peek(4).expr, val_peek(3).ident, val_peek(1).elist, val_peek(3).loc);
						if (val_peek(4).loc == null) {
							yyval.loc = val_peek(3).loc;
						}
					}
break;
case 47:
//#line 256 "Parser.y"
{
						yyval.expr = val_peek(0).lvalue;
					}
break;
case 50:
//#line 262 "Parser.y"
{
						yyval.expr = val_peek(0).expr;
					}
break;
case 51:
//#line 266 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.PLUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 52:
//#line 270 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MINUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 53:
//#line 274 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MUL, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 54:
//#line 278 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.DIV, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 55:
//#line 282 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MOD, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 56:
//#line 286 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.EQ, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 57:
//#line 290 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.NE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 58:
//#line 294 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 59:
//#line 298 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 60:
//#line 302 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 61:
//#line 306 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 62:
//#line 310 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.AND, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 63:
//#line 314 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.OR, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 64:
//#line 318 "Parser.y"
{
                		yyval = val_peek(1);
                	}
break;
case 65:
//#line 322 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NEG, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 66:
//#line 326 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NOT, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 67:
//#line 330 "Parser.y"
{
						yyval.expr = new Tree.Unary(Tree.RE, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 68:
//#line 334 "Parser.y"
{
						yyval.expr = new Tree.Unary(Tree.IM, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 69:
//#line 338 "Parser.y"
{
						yyval.expr = new Tree.Unary(Tree.COMPCAST, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 70:
//#line 342 "Parser.y"
{
                		yyval.expr = new Tree.ReadIntExpr(val_peek(2).loc);
                	}
break;
case 71:
//#line 346 "Parser.y"
{
                		yyval.expr = new Tree.ReadLineExpr(val_peek(2).loc);
                	}
break;
case 72:
//#line 350 "Parser.y"
{
                		yyval.expr = new Tree.ThisExpr(val_peek(0).loc);
                	}
break;
case 73:
//#line 354 "Parser.y"
{
						yyval.expr = new Tree.SuperExpr(val_peek(0).loc);
					}
break;
case 74:
//#line 358 "Parser.y"
{
                		yyval.expr = new Tree.NewClass(val_peek(2).ident, val_peek(3).loc);
                	}
break;
case 75:
//#line 362 "Parser.y"
{
                		yyval.expr = new Tree.NewArray(val_peek(3).type, val_peek(1).expr, val_peek(4).loc);
                	}
break;
case 76:
//#line 366 "Parser.y"
{
                		yyval.expr = new Tree.TypeTest(val_peek(3).expr, val_peek(1).ident, val_peek(5).loc);
                	}
break;
case 77:
//#line 370 "Parser.y"
{
                		yyval.expr = new Tree.TypeCast(val_peek(2).ident, val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 78:
//#line 374 "Parser.y"
{
						yyval.expr = new Tree.Dcopy(val_peek(1).expr, val_peek(3).loc);
					}
break;
case 79:
//#line 378 "Parser.y"
{
						yyval.expr = new Tree.Scopy(val_peek(1).expr, val_peek(3).loc);
					}
break;
case 80:
//#line 384 "Parser.y"
{
						yyval.expr = new Tree.Literal(val_peek(0).typeTag, val_peek(0).literal, val_peek(0).loc);
					}
break;
case 81:
//#line 388 "Parser.y"
{
						yyval.expr = new Null(val_peek(0).loc);
					}
break;
case 83:
//#line 395 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.elist = new ArrayList<Tree.Expr>();
                	}
break;
case 84:
//#line 402 "Parser.y"
{
						yyval.elist.add(val_peek(0).expr);
					}
break;
case 85:
//#line 406 "Parser.y"
{
                		yyval.elist = new ArrayList<Tree.Expr>();
						yyval.elist.add(val_peek(0).expr);
                	}
break;
case 86:
//#line 413 "Parser.y"
{
						yyval.stmt = new Tree.WhileLoop(val_peek(2).expr, val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 87:
//#line 419 "Parser.y"
{
						yyval.stmt = new Tree.ForLoop(val_peek(6).stmt, val_peek(4).expr, val_peek(2).stmt, val_peek(0).stmt, val_peek(8).loc);
					}
break;
case 88:
//#line 425 "Parser.y"
{
						yyval.expr = new Tree.Case(val_peek(5).expr, val_peek(2).slist, val_peek(1).stmt, val_peek(7).loc);
					}
break;
case 89:
//#line 430 "Parser.y"
{
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 90:
//#line 434 "Parser.y"
{
						yyval = new SemValue();
						yyval.slist = new ArrayList<Tree>();
					}
break;
case 91:
//#line 440 "Parser.y"
{
						yyval.stmt = new CaseItem(val_peek(3).expr, val_peek(1).expr, val_peek(3).loc);
					}
break;
case 92:
//#line 445 "Parser.y"
{
						yyval.stmt = new DefaultItem(val_peek(1).expr, val_peek(3).loc);
					}
break;
case 93:
//#line 450 "Parser.y"
{
						yyval.stmt = new Tree.Break(val_peek(0).loc);
					}
break;
case 94:
//#line 456 "Parser.y"
{
						yyval.stmt = new Tree.If(val_peek(3).expr, val_peek(1).stmt, val_peek(0).stmt, val_peek(5).loc);
					}
break;
case 95:
//#line 462 "Parser.y"
{
						yyval.stmt = val_peek(0).stmt;
					}
break;
case 96:
//#line 466 "Parser.y"
{
						yyval = new SemValue();
					}
break;
case 97:
//#line 472 "Parser.y"
{
						yyval.stmt = new Tree.Return(val_peek(0).expr, val_peek(1).loc);
					}
break;
case 98:
//#line 476 "Parser.y"
{
                		yyval.stmt = new Tree.Return(null, val_peek(0).loc);
                	}
break;
case 99:
//#line 482 "Parser.y"
{
						yyval.stmt = new Print(val_peek(1).elist, val_peek(3).loc);
					}
break;
case 100:
//#line 488 "Parser.y"
{
						yyval.stmt = new PrintComp(val_peek(1).elist, val_peek(3).loc);
					}
break;
case 101:
//#line 493 "Parser.y"
{
						yyval.stmt = new DoStmt(val_peek(1).slist, val_peek(2).loc);
					}
break;
case 102:
//#line 498 "Parser.y"
{
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 103:
//#line 502 "Parser.y"
{
						yyval.slist = new ArrayList<Tree>();
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 104:
//#line 508 "Parser.y"
{
						yyval.stmt = new DoSubStmt(val_peek(2).expr, val_peek(0).stmt, val_peek(2).loc);
					}
break;
//#line 1431 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    //if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      //if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        //if (yychar<0) yychar=0;  //clean, if necessary
        //if (yydebug)
          //yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      //if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
