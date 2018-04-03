var express = require('express');
var router = express.Router();
var utils = require('../config/utils');
var utilsQrCode = require('../config/utils_qrCode');
var moment = require('moment');
 
router.post('/qrGenerate', function(req, res) {
	utilsQrCode.generateQrCode(req.body['machineID']).then(function(response,err) {
		try{
			if(response.status == "success") {
                res.send({
                    status : 'success',
                    path : response.imagePath
                });
            }
		} catch(err) {
                console.log(err);
        }
	});
});

module.exports = router;