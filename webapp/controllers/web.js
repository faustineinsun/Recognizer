var express = require('express')

// find static files in ./public
var app = express();
app.set('views', __dirname + '/../views');
app.use(express.static(__dirname + '/../public'));
app.engine('html', require('ejs').renderFile);

app.get('/', function(req, res) {
      res.render('index.html');
});

app.set('port', (process.env.PORT || 5000))
app.listen(app.get('port'), function() {
      console.log("Node app is running at localhost:" + app.get('port'))
})
