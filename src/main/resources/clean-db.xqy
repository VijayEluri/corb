xquery version "1.0-ml";

for $doc in doc()
return xdmp:document-delete(xdmp:node-uri($doc))