grammar FGARewriter;

parse
    : expression EOF
    ;

expression
    : orExpr
    ;

orExpr
    : andExpr (OR andExpr)*
    ;

andExpr
    : primaryExpr (AND primaryExpr)*
    ;

primaryExpr
    : tupleToUsersetExpr
    | fromExpr
    | listExpr
    | relationExpr
    | SELF
    | LPAREN expression RPAREN
    ;

tupleToUsersetExpr
    : TUPLE_TO_USERSET COLON kvPairs
    ;

kvPairs
    : kvPair (SEMI kvPair)*
    ;

kvPair
    : ID EQ value
    ;

fromExpr
    : relationExpr FROM ID
    ;

listExpr
    : LBRACK listItems RBRACK
    ;

listItems
    : relationExpr (COMMA relationExpr)*
    ;

relationExpr
    : ID (HASH ID)?
    ;

value
    : ID (HASH ID)?
    ;


// Tokens
OR      : [Oo][Rr];
AND     : [Aa][Nn][Dd];
FROM    : [Ff][Rr][Oo][Mm];
SELF    : [Ss][Ee][Ll][Ff];
TUPLE_TO_USERSET : [Tt][Uu][Pp][Ll][Ee][Tt][Oo][Uu][Ss][Ee][Rr][Ss][Ee][Tt];

EQ      : '=';
COLON   : ':';
SEMI    : ';';
COMMA   : ',';
HASH    : '#';
LPAREN  : '(';
RPAREN  : ')';
LBRACK  : '[';
RBRACK  : ']';

ID      : [a-zA-Z0-9_\\-\\.]+;
WS      : [ \t\r\n]+ -> skip;
