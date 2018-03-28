var mongoose = require('mongoose');

var QrCodeSchema = mongoose.Schema({
	machineid : String,
	qrcode : { 
        data: Buffer, 
        contentType: String 
      },
      created: {
        type: Date,
        default: Date.now
      },
});

// create the model for users and expose it to our app
module.exports = mongoose.model('QrCollection', QrCodeSchema);
