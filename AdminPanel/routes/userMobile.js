var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var moment = require('moment');

/* GET users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addUserMobile', { title: 'Users',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utils.listAllMobileUsers().then(function(response) {

        res.render('viewUserMobile', {
            user: req.user,
            title: '',
            response: response,
            moment: moment,
        });
    });
});

router.post('/add',function(req, res) {
    // save user in database
    console.log(req.body);
    utils.addNewMobileUser(req.body['firstname'],
        req.body['lastname'],
        req.body['username'],
        req.body['usertype'],
        req.body['password'],).then(function(response,err) {
        	console.log("error = ", err);
        	console.log("response = ", response);
        	console.log("after saving to db --- response = ", response);
        	if (response.status == "success") {
            	res.render('addUserMobile', {
		            user: req.user,
		            status: 'success',
        		});
			}
			if (response.status == "userAlreadyExist") {
            	res.render('addUserMobile', {
		            user: req.user,
		            status: 'userAlreadyExist',
        		});
			}
		});
});

router.delete('/delete/:username',function(req,res) {
    console.log("username = ",req.params.username);
    console.log("req body = ", req.body);
    utils.deleteMobileUser(req.params['username']).then(function(response,err) {
        console.log("error = ", err);
        console.log("response = ", response);
        if(response.status == "success") {
            res.json({
                status : 'success',
            });
        }
        else if(response.status == "noUserFound") {
            res.json({
                status : 'noUserFound'
            });
        }
    });
});


module.exports = router;
