xquery version "1.0-ml";

(:~ 
 : A Simple URIS-MODULE example from the Corb Documentation 
 :)

let $uris := cts:uris('', 'document')
return (count($uris), $uris)