var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsMachineSubCategory = require('../config/utils_machineSubCategory');
var moment = require('moment');

/* GET users listing. */
router.get('/add', utils.isLoggedIn,function(req, res) {
  res.render('addMachineSubCategory', { title: 'Users',  user : req.user, status : null });
});


router.get('/list', utils.isLoggedIn, function(req, res) {
    utilsMachineSubCategory.listAllMachineSubCategory().then(function(response) {

        res.render('viewMachineSubCategory', {
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
    utilsMachineSubCategory.addNewMachineSubCategory(req.body['machinesubcategory']).then(function(response,err) {
            try{
            	console.log("error = ", err);
            	console.log("response = ", response);
            	console.log("after saving to db --- response = ", response);
            	if (response.status == "success") {
                	res.render('addMachineSubCategory', {
    		            user: req.user,
    		            status: 'success',
            		});
    			}
    			if (response.status == "machineSubCategoryAlreadyExist") {
                	res.render('addMachineSubCategory', {
    		            user: req.user,
    		            status: 'machineSubCategoryAlreadyExist',
            		});
    			}
            } catch(err) {
                console.log(err);
            }
		});
});


router.delete('/delete/:id', utils.isLoggedIn, function(req,res) {
    console.log("Object Id = ",req.params.id);
    utilsMachineSubCategory.deleteMachineSubCategory(req.params.id).then(function(response,err) {
        try {
            console.log("error = ", err);
            console.log("response = ", response);
            if(response.status == "success") {
                res.json({
                    status : 'success',
                });
            }
            else if(response.status == "noMachineSubCategoryFound") {
                res.json({
                    status : 'noMachineSubCategoryFound'
                });
            }
        } catch(err) {
            console.log(err);
        }
    });
});

//temproraliy not using the update function
// router.get('/update/:id', utils.isLoggedIn, function(req, res) {
//     utilsMachineSubCategory.getMachineSubCategory(req.params.id).then(function(response,err) {
//         try{
//             if(response.status == "success") {
//                 res.render('updateMachineSubCategory', {
//                     user : req.user,
//                     response : response,
//                     status : 'success'
//                 });
//             }
//             else if(response.status == "noMachineSubCategoryFound") {
//                 res.render('updateMachineSubCategory', {
//                     user : req.user,
//                     response : response,
//                     status : 'noMachineSubCategoryFound'
//                 });
//             }
//         } catch(err) {
//             console.log(err);
//         }
//     });
// });


// router.post('/update/:id', utils.isLoggedIn, function(req,res) {
//     console.log("id = ",req.params.id);
//     console.log("req body = ", req.body);
//     utilsMachineSubCategory.updateMachineSubCategory(req.params.id).then(function(response,err) {
//         try{
//             console.log("error = ", err);
//             console.log("response = ", response);
//             if(response.status == "success") {
//                 res.render('updateMachineSubCategory.ejs',{
//                     user: req.user,
//                     response : response,
//                     status : 'success',
//                 });
//             }
//             else if(response.status == "noMachineSubCategoryFound") {
//                 res.render( 'updateMachineSubCategory.ejs', {
//                     user: req.user,
//                     response : response,
//                     status : 'noMachineSubCategoryFound'
//                 });
//             }
//         } catch(err) {
//             console.log(err);
//         }
//     });
// });


module.exports = router;