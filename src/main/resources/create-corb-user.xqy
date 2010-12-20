xquery version "1.0-ml";

import module namespace sec="http://marklogic.com/xdmp/security" at 
    "/MarkLogic/security.xqy";

declare namespace my = "urn://eval-my";

declare variable $user-name as xs:string := "corb-user";
declare variable $user-description as xs:string := "CORB Application User";
declare variable $user-password as xs:string := "corb";

declare variable $role-name as xs:string := "corb-user";
declare variable $privileged-uri as xs:string := "/corb/";

let $options := 
<options xmlns="xdmp:eval">
  <database>{xdmp:security-database()}</database>
</options> 

let $create-role-query := '
  xquery version "1.0-ml"; 
  import module "http://marklogic.com/xdmp/security" at "/MarkLogic/security.xqy"; 
  declare namespace my = "urn://eval-my";
  declare variable $my:role-name as xs:string external;
  if (fn:not(/sec:role/sec:role-name[. eq $my:role-name])) then
    (
      sec:create-role($my:role-name, "Corb User",(),(),()),
      fn:concat("Role: ", $my:role-name, " created.")
    )
  else 
    fn:concat("Role: ", $my:role-name, " exists.")
'

let $create-privileges-and-permissions-query := '
  xquery version "1.0-ml"; 
  import module "http://marklogic.com/xdmp/security" at "/MarkLogic/security.xqy"; 
  declare namespace my = "urn://eval-my";
  declare variable $my:role-name as xs:string external;
  declare variable $my:privileged-uri as xs:string external;

  declare function local:privilege-exists($name as xs:string, $kind as xs:string) as xs:boolean {
    fn:boolean(/sec:privilege[sec:privilege-name eq $name][sec:kind eq $kind])
  };

  declare function local:role-has-permission($role-name as xs:string, $capability as xs:string) as xs:boolean {
    fn:boolean(
      sec:role-get-default-permissions($role-name)/sec:capability[. eq "update"]
    )
  };
    
  declare function local:privilege-add-role($action as xs:string, $kind as xs:string, $role-name as xs:string) {
    if (fn:not(local:privilege-has-role($action, $kind, $role-name))) then 
        (  
          sec:privilege-add-roles($action, $kind, $role-name),
          fn:concat("Role granted privilege: ", $action, ", ", $kind)
        )
    else 
        fn:concat("Role has privilege: ", $action, "/", $kind)
  };

  declare function local:privilege-has-role($action as xs:string, $kind as xs:string, $role-name) as xs:boolean {
    fn:boolean(
      /sec:privilege
       [sec:role-ids/sec:role-id eq sec:get-role-ids($role-name)]
       [sec:action eq $action]
       [sec:kind eq $kind]
    )
  };

  if (fn:not(local:privilege-exists($my:role-name, "uri"))) then 
    (
      sec:create-privilege($my:role-name, 
            $my:privileged-uri, 
            "uri", 
            $my:role-name),
      fn:concat("URI privilege: ", $my:privileged-uri, " created for role: ", $my:role-name)
    )
  else 
    local:privilege-add-role(
           $my:privileged-uri, 
            "uri", 
            $my:role-name),

  local:privilege-add-role(
         "http://marklogic.com/xdmp/privileges/xdbc-eval",
         "execute",
         $my:role-name),
  local:privilege-add-role(
         "http://marklogic.com/xdmp/privileges/xdbc-eval-in",
         "execute",
         $my:role-name),
  local:privilege-add-role(
         "http://marklogic.com/xdmp/privileges/xdbc-insert-in",
         "execute",
         $my:role-name)
  ,
  local:privilege-add-role(
         "http://marklogic.com/xdmp/privileges/xdbc-invoke",
         "execute",
         $my:role-name)
  ,  

  if (fn:not(local:role-has-permission($my:role-name, "update"))) then 
    sec:role-set-default-permissions(
          $my:role-name, 
          (
            xdmp:permission($my:role-name, "update"),
            xdmp:permission($my:role-name, "read"),
            xdmp:permission($my:role-name, "execute"),
            xdmp:permission($my:role-name, "insert")
          )
    )
  else 
    ()
'

let $create-user-query := '
  xquery version "1.0-ml"; 
  import module "http://marklogic.com/xdmp/security" at "/MarkLogic/security.xqy"; 
  declare namespace my = "urn://eval-my";
  declare variable $my:user-name as xs:string external;
  declare variable $my:user-description as xs:string external;
  declare variable $my:user-password as xs:string external;
  declare variable $my:role-name as xs:string external;
 
 if (fn:not(/sec:user/sec:user-name[. eq "corb-user"])) then
 (
    sec:create-user(
                $my:user-name, 
                $my:user-description,
                $my:user-password,
                $my:role-name, 
                (), (: permissions :)
                () (: collections :)
                ),
    fn:concat("User: ", $my:user-name, " created.")  
 ) 
 else 
    fn:concat("User: ", $my:user-name, " exists.")
'   

return 
(
  xdmp:eval(
    $create-role-query,
    (
      xs:QName("my:role-name"), $role-name
    ), 
    $options
  ),

  xdmp:eval(
    $create-privileges-and-permissions-query, 
    (
      xs:QName("my:role-name"), $role-name, 
      xs:QName("my:privileged-uri"), $privileged-uri
    ), 
    $options
  ),
  
  xdmp:eval(
    $create-user-query,
    (
      xs:QName("my:user-name"), $user-name,
      xs:QName("my:user-description"), $user-description,
      xs:QName("my:user-password"), $user-password,
      xs:QName("my:role-name"), $role-name
    ),
    $options  
  )
)   
