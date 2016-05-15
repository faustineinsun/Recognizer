var express = require('express')
var fs = require("fs")
var bodyParser = require('body-parser')
var sys = require("sys")
var exec = require("child_process").exec
var uuid = require('uuid')

// MongoDB
var mongojs = require('mongojs')
var mongodb = mongojs('recognizer', ['recognizer']) // database name: recognizer
mongodb.on('error', function (err) {
  console.log('<<< MongoDB error', err)
})
mongodb.on('connect', function () {
  console.log('<<< MongoDB connected')
})

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

app.get('/generatetimebaseduuid', function(req, res) {
  var timeUUID = uuid.v1();
  res.send(timeUUID);
  console.log("\n<<< Generated a time-based uuid: " + timeUUID);
});

app.get('/mongodbshowallrecords', function(req, res) {
  mongodb.recognizer.find({}, function (err, result) {
    console.log("<<< result, " + JSON.stringify(result));
  })
});

app.post('/saveimgtomongodb', function(req, res) {
  mongodb.recognizer.insert(req.body, function(err, result) {
    if(err) {
      throw err;
    }
    //console.log(JSON.stringify(result))
    console.log("<<< Saved {\"uuid\": "+req.body.uuid+", \"imgdataurl\": imgdataurl, \"predictdigit\":\"-1\"} to MongoDB");
  });

  // generate png image from image url
  var base64Data = req.body.imgdataurl.replace(/^data:image\/png;base64,/, "");
  var filePath = "public/img/out.png";
  fs.writeFile(filePath, base64Data, 'base64', function(err) {
    if (err) {
      return console.error(err);
    }
    console.log("Upated file: "+filePath);
  });

  // running image digit prediction on Spark
  function puts(error, stdout, stderr) {
    sys.puts(stdout)
  }
  console.log(">>> Predicting digit...");
  exec("${RECOGNIZER_PRJ_DIR}scripts/recognizeDigit.sh", puts)
});

app.set('port', (process.env.PORT || 5000))
app.listen(app.get('port'), function() {
  console.log("Node app is running at localhost:" + app.get('port'))
})
