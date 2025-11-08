grammar OpenFGAModel;

// -------------------------------------------------------------------------
// 1. 语法模型的顶层结构
// -------------------------------------------------------------------------

model : MODEL IDENTIFIER typeDefinition+ ;

typeDefinition : TYPE IDENTIFIER typeRestriction? relationBlock? ;

// Type Restrictions 规则
typeRestriction : FROM LPAREN typeRestrictionList RPAREN ;

typeRestrictionList : typeRestrictionItem (COMMA typeRestrictionItem)* ;

// Type Restriction 可以是 resource_type (e.g., 'document') 或 resource_type#relation (e.g., 'group#member')
typeRestrictionItem : IDENTIFIER (HASH relationName)? ;


relationBlock : RELATIONS defineStatement+ ;

// define 语句的定义: define <relationName> as <rewrite>
defineStatement : DEFINE relationName AS rewrite NEWLINE ;


// -------------------------------------------------------------------------
// 2. 关系重写表达式 (Rewrite Expression)
// -------------------------------------------------------------------------

rewrite : union ;

union : intersection (UNION intersection)* ;

intersection : exclusion (INTERSECTION exclusion)* ;

exclusion : primary (EXCLUSION primary)* ;


// -------------------------------------------------------------------------
// 3. 关系表达式的基本单元 (Primary)
// -------------------------------------------------------------------------
primary :
    SELF
    | relationName
    | computedUserset                     // Computed Userset, 例如: 'parent#editor' 或 'group:admin#member'
    | tupleToUserset                      // TTU 引用, 例如: 'editor from parentFolder'
    | LPAREN rewrite RPAREN
    ;


// -------------------------------------------------------------------------
// 4. 核心组件的解析规则
// -------------------------------------------------------------------------

// 🌟 修正: relationName 规则不变
relationName : IDENTIFIER ;

// 4.1. Computed Userset (e.g., parent#editor 或 group:admin#member)
// 🌟 修正: 确保类型前缀和关系名都使用 relationName 规则
computedUserset : (relationName COLON)? relationName HASH relationName ;


// 4.2. Tuple To Userset (e.g., editor from parentFolder)
tupleToUserset : relationName FROM relationName ;


// -------------------------------------------------------------------------
// 5. 词法规则 (Lexer Rules)
// -------------------------------------------------------------------------

// 5.1. 关键字 (Keywords) - 区分大小写不敏感
MODEL : 'model';
TYPE : 'type';
RELATIONS : 'relations';
DEFINE : 'define';
AS : 'as';

UNION : 'or';
INTERSECTION : 'and';
EXCLUSION : 'but not';

SELF : 'self';
FROM : 'from';

// 5.2. 符号和分隔符
HASH : '#';
LPAREN : '(';
RPAREN : ')';
COLON : ':';
COMMA : ',';
NEWLINE : '\r'? '\n';

// 5.3. 标识符和空白符
IDENTIFIER : LETTER (LETTER | DIGIT | UNDERSCORE)* ;
fragment LETTER : [a-zA-Z];
fragment DIGIT : [0-9];
fragment UNDERSCORE : '_';

WS : [ \t]+ -> skip;
COMMENT : '//' .*? '\n' -> skip;