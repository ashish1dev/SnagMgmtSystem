var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsMachine = require('../config/utils_machine');
var moment = require('moment');

/* GET users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addMachine', { title: 'Users',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utilsMachine.listAllMachine().then(function(response) {

        res.render('viewMachine', {
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
    utilsMachine.addNewMachine(req.body['modelname']).then(function(response,err) {
            try{
            	console.log("error = ", err);
            	console.log("response = ", response);
            	console.log("after saving to db --- response = ", response);
            	if (response.status == "success") {
                	res.render('addMachine', {
    		            user: req.user,
    		            status: 'success',
            		});
    			}
    			if (response.status == "machineAlreadyExist") {
                	res.render('addMachine', {
    		            user: req.user,
    		            status: 'machineAlreadyExist',
            		});
    			}
            } catch(err) {
                console.log(err);
            }
		});
});


router.delete('/delete/:id', utils.isLoggedIn, function(req,res) {
    console.log("id = ",req.params.id);
    console.log("req body = ", req.body);
    utilsMachine.deleteMachine(req.params.id).then(function(response,err) {
        try {
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.json({
                    status : 'success',
                });
            }
            else if(response.status == "noMachineFound") {
                res.json({
                    status : 'noMachineFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    }); 
});


router.get('/update/:id', utils.isLoggedIn, function(req, res) {
    utilsMachine.getMachine(req.params.id).then(function(response,err) {
        try{
            if(response.status == "success") {
                res.render('updateMachine', {
                    user : req.user,
                    response : response,
                    status : 'success'
                });
            }
            else if(response.status == "noMachineFound") {
                res.render('updateMachine', {
                    user : req.user,
                    response : response,
                    status : 'noMachineFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});


router.post('/update/:id', utils.isLoggedIn, function(req,res) {
    console.log("inside update post route");
    console.log("id = ",req.params.id);
    console.log("req body = ", req.body);
    utilsMachine.updateMachine(req.params.id,
                               req.body['modelname']).then(function(response,err) {
        try{
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.render('updateMachine.ejs',{
                    user: req.user,
                    response : response,
                    status : 'success',
                });
            }
            else if(response.status == "noMachineFound") {
                res.render( 'updateMachine.ejs', {
                    user: req.user,
                    response : response,
                    status : 'noMachineFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});


module.exports = router;