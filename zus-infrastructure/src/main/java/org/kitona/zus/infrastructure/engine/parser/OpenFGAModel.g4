grammar OpenFGAModel;

// -------------------------------------------------------------------------
// 1. 语法模型的顶层结构
// -------------------------------------------------------------------------

// 顶层规则: 整个模型文件
model : MODEL IDENTIFIER typeDefinition+ EOF ;

// Type 块的定义: type <identifier> [ from (...) ] [ relations (...) ]
typeDefinition : TYPE IDENTIFIER typeRestriction? relationBlock? ;

// Type Restrictions (主体限制): from ( typeRestrictionList )
typeRestriction : FROM LPAREN typeRestrictionList RPAREN ;

// Type Restrictions 列表: folder, group#member
typeRestrictionList : typeRestrictionItem (COMMA typeRestrictionItem)* ;

// Type Restriction Item: resource_type (e.g., 'document') 或 resource_type#relation (e.g., 'group#member')
typeRestrictionItem : relationName (HASH relationName)? ;


// Relations 块的定义
relationBlock : RELATIONS defineStatement+ ;

// define 语句的定义: define <relationName> as <rewrite> (注意：FGA DSL 用 'as' 而非 ':')
defineStatement : DEFINE relationName AS rewrite NEWLINE ;


// -------------------------------------------------------------------------
// 2. 关系重写表达式 (Rewrite Expression)
// 优先级定义: or < and < but not
// -------------------------------------------------------------------------

rewrite : union ;
union : intersection (UNION intersection)* ;
intersection : exclusion (INTERSECTION exclusion)* ;
exclusion : primary (EXCLUSION primary)* ;


// -------------------------------------------------------------------------
// 3. 关系表达式的基本单元 (Primary)
// -------------------------------------------------------------------------
primary :
    SELF                                  // self
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

// 5.1. 关键字 (Keywords) - 区分大小写不敏感，ANTLR 默认是匹配最长规则，但我们这里明确定义
MODEL : 'model';
TYPE : 'type';
RELATIONS : 'relations';
DEFINE : 'define';
AS : 'as';

// 集合操作符 (大小写不敏感)
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

LBRACK : '[';             // 左方括号
RBRACK : ']';             // 右方括号

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

// 5.4. 忽略空格和注释
WS : [ \t]+ -> skip;

// 注释（单行注释）
COMMENT : '//' ~[\r\n]* -> skip;

// -------------------------------------------------------------------------
// 6. 错误处理 (Optional, but recommended)
// -------------------------------------------------------------------------
// 确保所有不能识别的字符都被捕获，防止解析器停顿
UNRECOGNIZED_CHAR : . ;
