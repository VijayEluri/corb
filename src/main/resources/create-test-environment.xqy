xquery version "1.0-ml";

import module namespace admin = "http://marklogic.com/xdmp/admin" 
          at "/MarkLogic/admin.xqy";

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
    let $config := (admin:save-configuration($config), $config)
    return $config
};


declare function local:create-databases(
      $config as element(configuration), 
      $NewDatabases as xs:string+)
    {
    for $NewDatabase in $NewDatabases
    let $config := local:create-database($config, $NewDatabase)
    let $config := (admin:save-configuration($config), $config)
    return $config
};

declare function local:setup-test-resources(){
(: Ugly :)
let $config := local:create-databases(admin:get-configuration(), ("unit-test-db", "unit-test-modules") )
let $config := local:create-forests($config, ("unit-test-forest01", "unit-test-modules-forest01") )
let $config := (admin:save-configuration($config), $config)
let $config := admin:database-attach-forest($config, xdmp:database("unit-test-db"), xdmp:forest("unit-test-forest01") )
let $config := admin:database-attach-forest($config, xdmp:database("unit-test-modules"), xdmp:forest("unit-test-modules-forest01") )
return admin:save-configuration($config)
};

(: step one - create databases and forests :)
local:setup-test-resources()
(: 
let $config := local:create-databases(admin:get-configuration(), ("unit-test-db", "unit-test-modules") )
:)
(:let $config := local:create-databases(admin:get-configuration(), ("new-test-db4", "new-test-db5", "new-test-db6"))
return admin:save-configuration($config)
:)
(:
let $config := local:create-forests(admin:get-configuration(), ("new-forest1", "new-forest2"))
return admin:save-configuration($config)
:)
(: step two - add uri lexicon (unless this can be done at the time) :)

(: step three - insert a bunch of random docs into the new db for testing :)

(: step four - create an application server and modules db for testing :)
