var qr = require('qr-image');
var Machine = require('../models/machine');
var Q = require('q');
var fs = require("fs");
var qr = require('qr-image'); 



var generateQrCode = function(machineID){
	var deferred = Q.defer();
	var qr_svg = qr.image(machineID, { type: 'svg' });
	var path = 'QrCodeImages/' +machineID+ '_qr.svg';
	qr_svg.pipe(require('fs').createWriteStream("./public/" + path));
	 
	var svg_string = qr.imageSync(machineID, { type: 'svg' });

	deferred.resolve({
		'status' : 'success',
		'imagePath' : path
	});
	return deferred.promise;
}

var dumpQrCode = function(machineID, path, contentTypeName) {
	var deferred = Q.defer();
	console.log("dumpqrcode is being called");
	Machine.update({'machineID' : machineID},
					{"$set" : {'qrCode.data' : fs.readFileSync("./public/" + path), 
					'qrCode.contentType' : contentTypeName, 
					'qrCodeImageName' : "../" +path}},
					{new : true},function(err,qrcode) {
						if (err) {
	     					deferred.reject(new Error(err));
	    				}
	    				if(qrcode && qrcode!=undefined) {
	    					deferred.resolve({
	    						'status' : 'success'
	    					});
	    				}
					});
	return deferred.promise;
}


module.exports = {generateQrCode : generateQrCode,
				  dumpQrCode : dumpQrCode
			  	};