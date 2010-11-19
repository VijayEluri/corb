xquery version "1.0-ml";

import module namespace admin = "http://marklogic.com/xdmp/admin" 
          at "/MarkLogic/admin.xqy";

(:
 : GLOBAL VARIABLES
 :)
 
declare variable $TEST_DB as xs:string := "unit-test-db";
declare variable $TEST_DB_MODULES as xs:string := "unit-test-modules";
declare variable $TEST_FOREST as xs:string := "unit-test-forest01";
declare variable $TEST_FOREST_MODULES as xs:string := "unit-test-modules-forest01";



declare function local:clone-database(  $config,
                                        $source-database-name,
                                        $target-database-name,
                                        $triggers-database-name ){
let $config :=
    if ( $triggers-database-name eq "none" )
    then $config
    else local:clone-database( $config, "Triggers", $triggers-database-name, "none" )
        
let $config := admin:forest-copy($config, admin:forest-get-id( $config, $source-database-name ), $target-database-name, ())
let $config := admin:database-copy($config, admin:database-get-id( $config, $source-database-name ), $target-database-name)
let $config := (admin:save-configuration($config), $config)
let $config := admin:database-attach-forest($config, xdmp:database($target-database-name), xdmp:forest($target-database-name) )
let $config :=
        if ( $triggers-database-name eq "none" )
        then $config
        else admin:database-set-triggers-database($config, admin:database-get-id( $config, $target-database-name ), admin:database-get-id( $config, $triggers-database-name ))
return 
    admin:save-configuration($config)
}; 

(:local:clone-database(admin:get-configuration(), "test", "test2", "none"):)

declare function local:create-forest(
    $config as element(configuration),
    $NewForest as xs:string) as element(configuration)
    {
    try {

    (: Get all of the existing forests :)
         let $ExistingForests :=
             for $id in admin:get-forest-ids($config)
                return admin:forest-get-name($config, $id)

    (: Check to see if forest already exists. If not, create new forest :)
         let $config :=
            if ($NewForest = $ExistingForests) 
            then $config
            else 
              admin:forest-create(
                $config, 
                $NewForest, 
                xdmp:host(), 
                "")

         return $config

    } catch($e) {
        xdmp:log($e)
    }
};

declare function local:create-database(
    $config as element(configuration), 
    $NewDatabase as xs:string) as element(configuration)
    {
    try {
         let $security-db := admin:database-get-id($config, "Security")
         let $schema-db :=   admin:database-get-id($config, "Schemas")

    (: Get all of the existing databases :)
         let $ExistingDatabases :=
             for $id in admin:get-database-ids($config)
                return admin:database-get-name($config, $id)

    (: Check to see if database already exists. If not, create new 
       database :)
         let $config :=
            if ($NewDatabase = $ExistingDatabases) 
            then $config
            else  
              admin:database-create(
                $config, 
                $NewDatabase, 
                $security-db, 
                $schema-db)

         return $config

    } catch($e) {
         xdmp:log($e)
    }
};

declare function local:create-forests(
      $config as element(configuration), 
      $NewForests as xs:string+) 
    {
    for $NewForest in $NewForests
        let $config := local:create-forest($config, $NewForest)
        return (admin:save-configuration($config), $config)
        
};


declare function local:create-databases(
      $config as element(configuration), 
      $NewDatabases as xs:string+) 
    {
        for $NewDatabase in $NewDatabases
            return local:create-database($config, $NewDatabase)  
};

(:
 :
 :
 :              TEARDOWN UNIT TEST MODULE CODE BELOW:
 :
 :
 :
 :)

declare function local:delete-forest(
    $config as element(configuration), 
    $ForestName as xs:string) as element(configuration)
    {
    try {

         let $forest := admin:forest-get-id($config, $ForestName)

    (: Get all of the existing forests :)
         let $ExistingForests :=
             for $id in admin:get-forest-ids($config)
                return admin:forest-get-name($config, $id)

    (: Check to see if forest exists. If so, remove the forest :)
         let $config :=
            if ($ForestName = $ExistingForests) 
            then admin:forest-delete(
                   $config, 
                   admin:forest-get-id($config, $ForestName),
                   fn:true())
            else $config

         return $config

    } catch($e) {
         xdmp:log($e)
    }
};

declare function local:delete-database(      
    $config as element(configuration), 
    $DatabaseName as xs:string) as element(configuration) 
    {
    try {
         let $database := admin:database-get-id($config, $DatabaseName)
    (: Get all of the existing databases :)
         let $ExistingDatabases :=
             for $id in admin:get-database-ids($config)
                return admin:database-get-name($config, $id)

    (: Check to see if database exists. If so, remove the database :)
         let $config :=
            if ($DatabaseName = $ExistingDatabases) 
            then admin:database-delete(
                   $config, 
                   admin:database-get-id($config, $DatabaseName))
            else $config
         return $config
    } catch($e) {
         xdmp:log($e)
    }
};

declare function local:delete-forests(
      $config as element(configuration), 
      $forests as xs:string+)
    {
    for $forest in $forests
    let $config := local:delete-forest($config, $forest)
    return (admin:save-configuration($config), $config)
};

declare function local:delete-databases(
      $config as element(configuration), 
      $databases as xs:string+) as element(configuration)
    {
    for $database in $databases
    let $config := local:delete-database($config, $database)
    return (admin:save-configuration($config), $config)
};

declare function local:detach-forest(
    $config as element(configuration), 
    $db-name as xs:string,
    $forest-name as xs:string) as element(configuration)
    {  
    try {
        let $forests := admin:database-get-attached-forests($config, xdmp:database($db-name)) 
        let $config := 
            if (xdmp:forest($forest-name) = $forests) 
            then admin:database-detach-forest($config, xdmp:database($db-name), xdmp:forest($forest-name) )
            else $config
        return $config
    } catch($e) {
         xdmp:log($e)
    }
};


(:~
 : The function that does all the work...
 : TODO = attach forest doesn't work properly if a forest is already attached..
 :)
declare function local:setup-test-resources(){
    let $log := xdmp:log("Setting up...")
    let $config := admin:get-configuration()
    let $log := xdmp:log("1. creating dbs")
    let $config := local:create-database($config, $TEST_DB)
    let $config := local:create-database($config, $TEST_DB_MODULES) 
    (:let $log := xdmp:log(fn:concat("- DATABASES Created: ", xdmp:database($TEST_DB), ", ", xdmp:database($TEST_DB_MODULES))) :)
    let $log := xdmp:log("2. creating forests")
    let $config := local:create-forest($config, $TEST_FOREST)
    let $config := local:create-forest($config, $TEST_FOREST_MODULES) 
    let $log := xdmp:log("3. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    (:let $log := xdmp:log(fn:concat("- FORESTS Created: ", xdmp:forest($TEST_FOREST), ", ", xdmp:forest($TEST_FOREST_MODULES))):)
    (: Enable the URI lexicon on the test-db for CORB :)
    let $log := xdmp:log("4. Enabling URI Lexicon on db")
    let $config := admin:database-set-uri-lexicon($config, xdmp:database($TEST_DB), fn:true())
    let $log := xdmp:log("5. Attaching Test DB forests...")
    let $config := admin:database-attach-forest($config, xdmp:database($TEST_DB), xdmp:forest($TEST_FOREST) )
    let $log := xdmp:log("6. Attaching Test DB MODULE forests...")
    let $config := admin:database-attach-forest($config, xdmp:database($TEST_DB_MODULES), xdmp:forest($TEST_FOREST_MODULES) )
    let $log := xdmp:log("7. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    let $log := xdmp:log("Setup Complete")
    return $config
};

(:~
 : The function that tears down...
 :)
declare function local:teardown-test-resources(){
    let $log := xdmp:log("Tearing down...")   
    let $config := admin:get-configuration()
    let $log := xdmp:log("1. Detaching test forests")
    let $config := local:detach-forest($config, $TEST_DB, $TEST_FOREST) 
    let $config := local:detach-forest($config, $TEST_DB_MODULES, $TEST_FOREST_MODULES) 
    let $log := xdmp:log("2. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    let $log := xdmp:log("3. Deleting Forests")
    let $config := local:delete-forest($config, $TEST_FOREST)
    let $config := local:delete-forest($config, $TEST_FOREST_MODULES) 
    let $log := xdmp:log("4. Deleting Databases")
    let $config := local:delete-database($config, $TEST_DB)
    let $config := local:delete-database($config, $TEST_DB_MODULES) 
    let $log := xdmp:log("5. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    let $log := xdmp:log("6. Teardown complete")
    return $config 
};

(:::
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

(: step one - create databases and forests and add lexicons :)
(:local:setup-test-resources():)

(: step two - insert a bunch of random docs into the new db for testing :)
local:create-test-documents(2000)
(: step three - create an application server and modules db for testing :)


(: step four - remove databases and forests :)
(:local:teardown-test-resources():)