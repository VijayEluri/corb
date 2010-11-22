xquery version "1.0-ml";

declare variable $URI as xs:string external;

xdmp:log(fn:concat("[CORB] :: basic-transform-module :: processing: ", $URI))