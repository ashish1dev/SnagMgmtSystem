var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsMachineCategory = require('../config/utils_machineCategory');
var moment = require('moment');

/* GET users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addMachineCategory', { title: 'Users',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utilsMachineCategory.listAllMachineCategory().then(function(response) {

        res.render('viewMachineCategory', {
            user: req.user,
            title: '',
            response: response,
        });
    });
});


router.post('/add', utils.isLoggedIn, function(req, res) {
    // save machine category in database
    console.log(req.body);
    utilsMachineCategory.addNewMachineCategory(req.body['machineCategory']).then(function(response,err) {
            try{
            	console.log("error = ", err);
            	console.log("response = ", response);
            	console.log("after saving to db --- response = ", response);
            	if (response.status == "success") {
                	res.render('addMachineCategory', {
    		            user: req.user,
    		            status: 'success',
            		});
    			}
    			if (response.status == "machineCategoryAlreadyExist") {
                	res.render('addMachineCategory', {
    		            user: req.user,
    		            status: 'machineCategoryAlreadyExist',
            		});
    			}
            } catch(err) {
                console.log(err);
            }
		});
});


router.delete('/delete/:id', utils.isLoggedIn, function(req,res) {
    console.log("Object Id = ",req.params.id);
    utilsMachineCategory.deleteMachineCategory(req.params.id).then(function(response,err) {
        try {
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.json({
                    status : 'success',
                });
            }
            else if(response.status == "noMachineCategoryFound") {
                res.json({
                    status : 'noMachineCategoryFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});

module.exports = router;