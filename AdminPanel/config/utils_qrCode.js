var qr = require('qr-image');
var Machine = require('../models/machine');
var Q = require('q');
var fs = require("fs");



var generateQrCode = function(machineID){
	var deferred = Q.defer();
	var qr = require('qr-image'); 
	var qr_svg = qr.image(machineid, { type: 'svg' });
	var path = './QrCodeImages/' +machineid+ '_qr.svg';
	qr_svg.pipe(require('fs').createWriteStream(path));
	 
	var svg_string = qr.imageSync(machineid, { type: 'svg' });

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
					{"$set" : {'qrcode.data' : fs.readFileSync(path), 'qrcode.contentType' : contentTypeName}},
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