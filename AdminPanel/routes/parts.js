var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsParts = require('../config/utils_parts');
var moment = require('moment');

/* GET Users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addParts', { title: 'parts',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utilsParts.listAllParts().then(function(response) {

        res.render('viewParts', {
            user : req.user,
            title: '',
            response: response,
            moment: moment,
        });
    });
});


router.post('/add', utils.isLoggedIn, function(req, res) {
    // save parts in database
    console.log(req.body);
    utilsParts.addNewParts(req.body['partName']).then(function(response,err) {
            try{
            	console.log("error = ", err);
            	console.log("response = ", response);
            	console.log("after saving to db --- response = ", response);
            	if (response.status == "success") {
                	res.render('addParts', {
    		            user: req.user,
    		            status: 'success',
            		});
    			}
    			if (response.status == "partsAlreadyExist") {
                	res.render('addParts', {
    		            user: req.user,
    		            status: 'partsAlreadyExist',
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
    utilsParts.deleteParts(req.params.id).then(function(response,err) {
        try {
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.json({
                    status : 'success',
                });
            }
            else if(response.status == "noPartsFound") {
                res.json({
                    status : 'noPartsFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    }); 
});

router.get('/listAllCategory', function(req, res) {
    utilsParts.getAllCategory().then(function(response) {
        console.log("response = ",response);

        res.json({
            user : req.user,
            title: '',
            'status' : response.status,
            'category' : response.category,
            moment: moment,
        });
    });
});

router.get('/listAllSubCategory', function(req, res) {
    utilsParts.getAllSubCategory().then(function(response) {

        res.json({
            user : req.user,
            title: '',
            'status' : response.status,
            'subCategory' : response.subCategory,
            moment: moment,
        });
    });
});

module.exports = router;