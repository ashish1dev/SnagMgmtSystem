var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsMobileUser = require('../config/utils_mobileUser');
var moment = require('moment');

/* GET users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addUserMobile', { title: 'Users',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utilsMobileUser.listAllMobileUsers().then(function(response) {

        res.render('viewUserMobile', {
            user: req.user,
            title: '',
            response: response,
            moment: moment,
        });
    });
});


router.post('/add', utils.isLoggedIn, function(req, res) {
    // save user in database
    console.log(req.body);
    utilsMobileUser.addNewMobileUser(req.body['firstName'],
        req.body['lastName'],
        req.body['userName'],
        req.body['userType'],
        req.body['password'],).then(function(response,err) {
            try{
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
            } catch(err) {
                console.log(err);
            }
		});
});


router.delete('/delete/:userName', utils.isLoggedIn, function(req,res) {
    console.log("username = ",req.params.username);
    console.log("req body = ", req.body);
    utilsMobileUser.deleteMobileUser(req.params['username']).then(function(response,err) {
        try {
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
        } catch(err) {
            console.log(err);
        }
    });
});


router.get('/update/:userName', utils.isLoggedIn, function(req, res) {
    utilsMobileUser.getMobileUser(req.params['userName']).then(function(response,err) {
        try{
            if(response.status == "success") {
                res.render('updateUserMobile', {
                    user : req.user,
                    response : response,
                    status : 'success'
                });
            }
            else if(response.status == "noUserFound") {
                res.render('updateUserMobile', {
                    user : req.user,
                    response : response,
                    status : 'noUserFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});
 

router.post('/update/:userName', utils.isLoggedIn, function(req,res) {
    console.log("inside update post route");
    console.log("username = ",req.params.userName);
    console.log("req body = ", req.body);
    utilsMobileUser.updateMobileUser(req.body['firstName'],
                           req.body['lastName'],
                           req.params['userName'],
                           req.body['userType'],
                           req.body['password'],).then(function(response,err) {
        try{
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.render('updateUserMobile.ejs',{
                    user: req.user,
                    response : response,
                    status : 'success',
                });
            }
            else if(response.status == "noUserFound") {
                res.render( 'updateUserMobile.ejs', {
                    user: req.user,
                    response : response,
                    status : 'noUserFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});

router.post('/authenticateMobileUser', function(req, res) {
    console.log("req body authenticateMobileUser= ", req.body);
    console.log("req params authenticateMobileUser= ", req.params);
    utilsMobileUser.authenticateMobileUser(req.body['userName'],
                                    req.body['password']).then(function(response, err) {


        try{
            if(response.status == "success") {
                res.json( {
                    'user' : response.user,
                    'status' : 'success'
                });
            }
            else if(response.status == "noUserFound") {
                res.json( {
                    'user' : response.user,
                    'status' : 'noUserFound'
                })
            }
        }catch (err) {
            console.log(err);
        }
    });
});


module.exports = router;