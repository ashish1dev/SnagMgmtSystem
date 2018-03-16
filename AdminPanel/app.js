  var express = require('express');
  var passport = require('passport');

  var engine = require('ejs-locals');
  var path = require('path');
  var favicon = require('serve-favicon');
  var logger = require('morgan');
  var cookieParser = require('cookie-parser');
  var bodyParser = require('body-parser');

  var routes = require('./routes/index');
  var login = require('./routes/login');
  var signup = require('./routes/signup');
  var logout = require('./routes/logout');
  var userMobile = require('./routes/userMobile');
  var machineCategory = require('./routes/machineCategory');
  var machineSubCategory = require('./routes/machineSubCategory');
  var machine = require('./routes/machine');

  var config = require('./config');
  var dbConfig = require('./models/db.js');
  var mongoose = require('mongoose');
  var expressSession = require('express-session');
  var flash = require('connect-flash');
  var app = express();

  mongoose.connect(dbConfig.url);

  require('./config/passport')(passport); // pass passport for configuration

  // view engine setup
  app.set('views', path.join(__dirname, 'views'));
  app.engine('ejs', engine);
  app.set('view engine', 'ejs');

  // uncomment after placing your favicon in /public
  //app.use(favicon(__dirname + '/public/favicon.ico'));
  app.use(logger('dev'));
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({
    extended: false
  }));
  app.use(cookieParser());
  app.use(express.static(path.join(__dirname, 'public')));

  // config passport
  app.use(expressSession({
    secret: 'mySecretKey'
  }));
  app.use(passport.initialize());
  app.use(passport.session());
  app.use(flash()); // use connect-flash for flash messages stored in session


  app.use('/', routes);
  
  app.use('/login', login);
  app.use('/signup', signup);
  app.use('/logout', logout);
  app.use('/userMobile', userMobile);
  app.use('/machine', machine);
  app.use('/machineCategory', machineCategory);
  app.use('/machineSubCategory', machineSubCategory);

  // catch 404 and forward to error handler
  app.use(function(req, res, next) {
    // var err = new Error('Not Found');
    // err.status = 404;
    // next(err);

    res.status(404);

    // respond with html page
    if (req.accepts('html')) {
      res.render('errors/404', {
        user: req.user,
        title: 'page not found error',
        url: req.url
      });
      return;
    }
  });

  // error handlers

  // development error handler
  // will print stacktrace
  if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
      res.status(err.status || 500);
      res.render('error', {
        message: err.message,
        error: err
      });
    });
  }

  // production error handler
  // no stacktraces leaked to user
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: {}
    });
  });


  module.exports = app;
