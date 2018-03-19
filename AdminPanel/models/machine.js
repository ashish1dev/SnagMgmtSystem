// load the things we need
var mongoose = require('mongoose');

// define the schema for our user model
var machineSchema = mongoose.Schema({
      machineid : String,
      qrcode : { 
        data: Buffer, 
        contentType: String 
      },
      modelname : String,
      created: {
        type: Date,
        default: Date.now
      },

});


// create the model for users and expose it to our app
module.exports = mongoose.model('MachineCollection', machineSchema);
