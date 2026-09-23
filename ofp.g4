grammar ofp;

@header {
    package generated;
}

// ---------- Parser ----------

// Exactly ONE main function, optionally surrounded by function declarations.
program : funcDecl* main funcDecl* ;

main : 'void' 'main' '(' ')' block ;

funcDecl : retType ID '(' params? ')' block ;

params : param (',' param)* ;

param
    : type ID                 # valueParam
    | arrayType '[' ']' ID    # arrayParam
    ;

type      : 'int' | 'float' | 'bool' | 'char' | 'string' ;
arrayType : 'int' | 'float' | 'char' ;
retType   : 'void' | type | arrayType '[' ']' ;

block : '{' stmt* '}' ;

stmt
    : declaration                                           # declStmt
    | ID '=' expr ';'                                       # assignStmt
    | ID '[' expr ']' '=' expr ';'                          # arrayAssignStmt
    | ('print' | 'println') '(' expr ')' ';'                # printStmt
    | funcCall ';'                                          # callStmt
    | 'while' '(' expr ')' (stmt | block)                   # whileStmt
    | 'if' '(' expr ')' (stmt | block)
      ('else' (stmt | block))?                              # ifStmt
    | 'return' expr? ';'                                    # returnStmt
    | block                                                 # blockStmt
    ;

declaration
    : type ID ('=' expr)? ';'                               # varDecl
    | arrayType '[' ']' ID ('=' expr)? ';'                  # arrayDecl
    ;

funcCall : ID '(' (expr (',' expr)*)? ')' ;

expr
    : expr '[' expr ']'                                     # indexExpr
    | expr '.' 'length'                                     # lengthExpr
    | '(' expr ')'                                          # parenExpr
    | 'new' arrayType '[' expr ']'                          # newArrayExpr
    | '{' (expr (',' expr)*)? '}'                           # arrayLiteralExpr
    | '-' expr                                              # negExpr
    | expr ('*' | '/') expr                                 # mulDivExpr
    | expr ('+' | '-') expr                                 # addSubExpr
    | expr ('<' | '>') expr                                 # compareExpr
    | expr '==' expr                                        # equalExpr
    | funcCall                                              # callExpr
    | INT                                                   # intExpr
    | FLOAT                                                 # floatExpr
    | BOOL                                                  # boolExpr
    | CHAR                                                  # charExpr
    | STRING                                                # stringExpr
    | ID                                                    # idExpr
    ;

// ---------- Lexer ----------

FLOAT   : ('0' | [1-9] [0-9]*) '.' [0-9]+ ;
INT     : '0' | [1-9] [0-9]* ;
BOOL    : 'true' | 'false' ;
CHAR    : '\'' ~['\\\r\n] '\'' ;
STRING  : '"' ~["\\\r\n]* '"' ;
ID      : [a-zA-Z] [a-zA-Z0-9_]* ;

COMMENT : '#' ~[\r\n]* -> skip ;
WS      : [ \t\r\n]+ -> skip ;
