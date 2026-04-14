grammar OpenFGAModel;

// -------------------------------------------------------------------------
// 1. 语法模型的顶层结构
// -------------------------------------------------------------------------

// 顶层规则: 整个模型文件
model : MODEL modelHeader typeDefinition* conditionBlock? EOF ;

// 兼容两种头部:
// - model schema 1.1
// - model <legacy_identifier>
modelHeader
    : SCHEMA schemaVersion
    | qualifiedName
    ;

schemaVersion : VERSION ;

// Type 块的定义: type <identifier> [ from (...) ] [ relations (...) ]
typeDefinition : TYPE qualifiedName typeRestriction? relationBlock? ;

// Type Restrictions (主体限制): from ( typeRestrictionList )
typeRestriction : FROM LPAREN typeRestrictionList RPAREN ;

// Type Restrictions 列表: folder, group#member
typeRestrictionList : typeRestrictionItem (COMMA typeRestrictionItem)* ;

// Type Restriction Item:
// - resource_type (e.g., 'document')
// - resource_type#relation (e.g., 'group#member')
// - typed wildcard (e.g., 'user:*')
typeRestrictionItem
    : typeRestrictionBase (WITH relationName)?
    ;

typeRestrictionBase
    : qualifiedName (HASH relationName)?
    | qualifiedName COLON STAR
    ;


// Relations 块的定义
relationBlock : RELATIONS defineStatement+ ;

// define 语句的定义: define <relationName> as|: <rewrite>
defineStatement : DEFINE relationName (AS | COLON) rewrite NEWLINE? ;

// Conditions 块
conditionBlock : CONDITIONS conditionDefinition+ ;

conditionDefinition
    : CONDITION relationName LPAREN conditionParameterList? RPAREN LBRACE conditionExpression RBRACE NEWLINE?
    ;

conditionParameterList : conditionParameter (COMMA conditionParameter)* ;

conditionParameter : relationName COLON conditionType ;

conditionType : qualifiedName (LT conditionType (COMMA conditionType)* GT)? ;

// 条件体作为透明 token 流承载，后续由 CEL 引擎解释。
conditionExpression : (.)*? ;


// -------------------------------------------------------------------------
// 2. 关系重写表达式 (Rewrite Expression)
// 优先级定义: or < and < but not
// -------------------------------------------------------------------------

rewrite : union ;
union : intersection (UNION intersection)* ;
intersection : exclusion (INTERSECTION exclusion)* ;
exclusion : primary (BUT NOT primary)* ;


// -------------------------------------------------------------------------
// 3. 关系表达式的基本单元 (Primary)
// -------------------------------------------------------------------------
primary :
    SELF                                  // self
    | THIS                                // this (OpenFGA alias)
    | relationName                        // 简单关系, 例如: 'owner'
    | computedUserset                     // Computed Userset, 例如: 'parent#editor' 或 'group:admin#member'
    | tupleToUserset                      // TTU 引用, 例如: 'editor from parentFolder'
    | usersetRestrictionList              // 主体集限制列表, 例如: [user, group#member]
    | LPAREN rewrite RPAREN               // 括号分组
    ;


// ------------------------------------------------------------------------
// 4. 核心组件的解析规则
// ------------------------------------------------------------------------

// 关系/类型/标识符名称
relationName : IDENTIFIER ;

qualifiedName : IDENTIFIER (DOUBLE_COLON IDENTIFIER)* ;

// 4.1. Computed Userset (e.g., parent#editor 或 group:admin#member)
// 格式: [类型名:] 关系名 # 目标关系名
computedUserset : (relationName COLON)? relationName HASH relationName ;


// 4.2. Tuple To Userset (e.g., editor from parentFolder)
// 格式: 目标关系名 FROM 元组关系名
tupleToUserset : relationName FROM relationName ;

// 4.3. Userset Restriction List (e.g., [user, group#member])
// 此规则解析方括号包裹的主体列表
usersetRestrictionList : LBRACK typeRestrictionItem (COMMA typeRestrictionItem)* RBRACK ;


// -------------------------------------------------------------------------
// 5. 词法规则 (Lexer Rules)
// -------------------------------------------------------------------------

// 5.1. 关键字 (Keywords)
MODEL : 'model';
SCHEMA : 'schema';
TYPE : 'type';
RELATIONS : 'relations';
DEFINE : 'define';
CONDITIONS : 'conditions';
CONDITION : 'condition';
AS : 'as';
WITH : 'with';

// 集合操作符
UNION : 'or';
INTERSECTION : 'and';
BUT : 'but';
NOT : 'not';

SELF : 'self';
THIS : 'this';
FROM : 'from';

// 5.2. 符号和分隔符
HASH : '#';
LPAREN : '(';
RPAREN : ')';
COLON : ':';
DOUBLE_COLON : '::';
COMMA : ',';
STAR : '*';
LT : '<';
GT : '>';

LBRACK : '[';             // 左方括号
RBRACK : ']';             // 右方括号
LBRACE : '{';
RBRACE : '}';

// 换行符（用于 define 语句的结束）
NEWLINE : ('\r'? '\n');

// 5.3. 标识符
// 允许字母、数字和下划线
IDENTIFIER
    : LETTER (LETTER | DIGIT | '_')*
    ;

fragment LETTER
    : [a-zA-Z]
    ;

fragment DIGIT
    : [0-9]
    ;

VERSION
    : DIGIT+ '.' DIGIT+
    ;

// 5.4. 忽略空格和注释
WS : [ \t]+ -> skip;

// 注释（单行注释）
COMMENT : '//' ~[\r\n]* -> skip;
