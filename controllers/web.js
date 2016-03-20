var express = require('express')
var fs = require("fs")
var bodyParser = require('body-parser');

// find static files in ./public
var app = express();
app.set('views', __dirname + '/../views');
app.use(express.static(__dirname + '/../public'));
app.engine('html', require('ejs').renderFile);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

app.get('/', function(req, res) {
  res.render('index.html');
});

app.post('/saveimg', function(request, response) {
  var base64Data = request.body.imgurl.replace(/^data:image\/png;base64,/, "");
  //console.log(base64Data);

  var filePath = "public/img/out.png";
  /*
  console.log("Going to delete an existing .png file: "+filePath);
  fs.unlink(filePath, function(err) {
    if (err) {
      return console.error(err);
    }
    console.log("File deleted successfully!");
  });
  */
  fs.writeFile(filePath, base64Data, 'base64', function(err) {
    if (err) {
      return console.error(err);
    }
    console.log("Upated file: "+filePath);
  });
});

app.set('port', (process.env.PORT || 5000))
app.listen(app.get('port'), function() {
  console.log("Node app is running at localhost:" + app.get('port'))
})
