var http = require('http');

var server = http.createServer(function (request, response) {
  response.writeHead(200, {"Content-Type": "text/plain"});
  console.log("Received HelloWorld!");
  response.end("Hello World!\n");
});

server.listen(8000);

console.log("Server running at http://localhost:8000/");
