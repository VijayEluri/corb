xquery version "1.0-ml";

declare variable $TEST_FOREST as xs:string := "unit-test-forest01";


(:
 :
 :
 :
 :          C R E A T E  T E S T  D A T A 
 :
 :
 :)

declare function local:random-hex($length as xs:integer) as xs:string { string-join( for $n in
    1 to $length return xdmp:integer-to-hex(xdmp:random(15)), "" ) };

declare function local:generate-uuid-v4() as xs:string {
    string-join(
        (
            local:random-hex(8),
            local:random-hex(4),    
            local:random-hex(4),
            local:random-hex(4),
            local:random-hex(12)
        ),
        "-"
    )
};



declare function local:create-test-documents($num-docs as xs:integer){
    let $log := xdmp:log(concat("Creating ", $num-docs, " Test Documents"))
    for $i in (1 to $num-docs) 
        let $id := local:generate-uuid-v4()
        return (
        xdmp:document-insert(
            fn:concat("/test/", $id, ".xml"), 
            element data {
                element id {$id},
                element date {fn:current-dateTime()}
            },
            xdmp:default-permissions(), 
            xdmp:default-collections(), 
            10, 
            (xdmp:forest($TEST_FOREST))
        )
    )     
};

(: step two - insert a bunch of random docs into the new db for testing :)
local:create-test-documents(2000)
