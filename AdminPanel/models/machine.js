// load the things we need
var mongoose = require('mongoose');
var autoIncrement = require('mongoose-auto-increment');
var qr = require('qr-image');

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


autoIncrement.initialize(mongoose.connection);
machineSchema.plugin(autoIncrement.plugin, {
  model: 'machineSchema',
  field: 'machineid',
  startAt: 1,
  incrementBy: 1
});

// create the model for users and expose it to our app
module.exports = mongoose.model('MachineCollection', machineSchema);
