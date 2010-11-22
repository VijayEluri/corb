xquery version "1.0-ml";

(:~
 :
 :
 :              TEARDOWN UNIT TEST MODULE CODE BELOW:
 :
 :
 :
 :)

import module namespace admin = "http://marklogic.com/xdmp/admin" 
          at "/MarkLogic/admin.xqy";

(:
 : GLOBAL VARIABLES
 :)
 
declare variable $TEST_DB as xs:string external;
declare variable $TEST_DB_MODULES as xs:string external;
declare variable $TEST_FOREST as xs:string external;
declare variable $TEST_FOREST_MODULES as xs:string external;
declare variable $BEGIN_TEARDOWN as xs:integer external;

(:
 : Functions
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
    let $log := xdmp:log("4. Removing XDBC Server")
    let $config := admin:appserver-delete($config,
        admin:appserver-get-id($config, admin:group-get-id($config, "Default"), "xdbc-9997-corbtest") )
    let $log := xdmp:log("5. Saving configuration (and restarting MarkLogic)")
    return admin:save-configuration($config)
};

declare function local:teardown-databases(){
    let $log := xdmp:log("6. Deleting Databases")
    let $config := admin:get-configuration()
    let $config := local:delete-database($config, $TEST_DB)
    let $config := local:delete-database($config, $TEST_DB_MODULES) 
    let $log := xdmp:log("7. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    let $log := xdmp:log("Teardown complete")
    return $config 
};

if ($BEGIN_TEARDOWN eq 1) 
then local:teardown-test-resources()
else local:teardown-databases()