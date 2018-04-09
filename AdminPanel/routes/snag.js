var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsSnag = require('../config/utils_snag');
var moment = require('moment');

/* GET Users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addSnag', { title: 'snags',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utilsSnag.listAllSnag().then(function(response) {

        res.render('viewSnag', {
            user : req.user,
            title: '',
            response: response,
            moment: moment,
        });
    });
});

router.post('/getSnagsBySnagType', function(req, res) {
    utilsSnag.listOfSnagBySnagType(req.body['currentStatusOfSnag']).then(function(response) {
        console.log("response in getSnagsBySnagType = ", response);

        if(response.status == "success"){
            res.json({
                'snags' : response.snags,
                'status' : "success"
            });
        }
        else if(response.status == "snagsNotFound"){
            res.json({
                'status' : "snagsNotFound"
            });
        }
    });
});


router.post('/add', function(req, res) {
    // save snag in database
    console.log(req.body);
    utilsSnag.addNewSnag(req.bpdy['snagID'],
                         req.body['machineID'],
                         req.body['category'],
                         req.body['subCategory'],
                         req.body['partName'],
                         req.body['description'],
                         req.body['inspector1UserName'],
                         req.body['functionalOperatorUserName'],
                         req.body['inspector2UserName'],
                         req.body['inspector3UserName'],
                         req.body['currentStatusOfSnag']).then(function(response,err) {
            try{
            	console.log("error = ", err);
            	console.log("response = ", response);
            	console.log("after saving to db --- response = ", response);
            	if (response.status == "success") {
                	res.json({
    		            status: 'success',
                        'snagID' : response.snagID
            		});
    			}
    			else if (response.status == "failed") {
                	res.json({
    		            status: 'failed',
            		});
    			}
            } catch(err) {
                console.log(err);
            }
		});
});


router.delete('/delete/:id', utils.isLoggedIn, function(req,res) {
    console.log("id = ",req.params.id);
    utilsSnag.deleteSnag(req.params.id).then(function(response,err) {
        try {
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.json({
                    status : 'success',
                });
            }
            else if(response.status == "noSnagFound") {
                res.json({
                    status : 'noSnagFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    }); 
});


router.get('/update/:id', utils.isLoggedIn, function(req, res) {
    utilsSnag.getSnag(req.params.id).then(function(response,err) {
        try{
            if(response.status == "success") {
                res.render('updateSnag', {
                    user : req.user,
                    response : response,
                    status : 'success'
                });
            }
            else if(response.status == "noSnagFound") {
                res.render('updateSnag', {
                    user : req.user,
                    response : response,
                    status : 'noSnagFound'
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
    utilsSnag.updateSnag(req.params.id,
                               req.body['description'],
                               req.body['inspector1UserName'],
                               req.body['functionalOperatorUserName'],
                               req.body['inspector2UserName'],
                               req.body['inspector3UserName'],
                               req.body['currentStatusOfSnag']).then(function(response,err) {
        try{
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.render('updateSnag.ejs',{
                    user: req.user,
                    response : response,
                    status : 'success',
                });
            }
            else if(response.status == "noSnagFound") {
                res.render( 'updateSnag.ejs', {
                    user: req.user,
                    response : response,
                    status : 'noSnagFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});

router.post('/updateCurrentStatus', function(req, res) {
    utilsSnag.updateCurrentStatus(req.body['snagID'],
                                  req.body['userName'],
                                  req.body['userType'],
                                  req.body['currentStatusOfSnag']).then(function(response,err) {
                                    console.log("response in updateCurrentStatus = ", response);
                                    if(response.status == "success") {
                                        res.json({
                                            'fetchCurrentStatus' : response.fetchCurrentStatus,
                                            status : 'success',
                                        });
                                    }
                                    else if(response.status == "noSnagFound") {
                                        res.json({
                                            'fetchCurrentStatus' : response.fetchCurrentStatus,
                                            status : 'noSnagFound'
                                        });
                                    }
                                  });
});


router.get('/listAllCategorySubCategoryParts', function(req, res) {
    utilsSnag.getAllCategorySubCategoryParts().then(function(response) {
        console.log("response = ",response);
        res.json({
            'status' : response.status,
            'result' : response.result,
        });
    });
});

router.post('/getMySnags', function(req,res) {
    utilsSnag.listMySnags(req.body['userType'],
                      req.body['userName']).then(function(response, err){
                        console.log("response in getMySnags = ", response);
                                    if(response.status == "success") {
                                        res.json({
                                            'snags' : response.snags,
                                            status : 'success',
                                        });
                                    }
                                    else if(response.status == "noSnagFound") {
                                        res.json({
                                            'snags' : response.snags,
                                            status : 'noSnagFound'
                                        });
                                    }
                                  });
});


module.exports = router;