var usb = Usb();

$("#init").on("click", function() {
  common.step([
    function(next) {
      console.log('opening device...');
      usb.open(next);
      console.log('device opened');
    },
    function(data, next) {
      var command = '0x04,0x00';
      console.log('sending command: ' + command);
      usb.write('0x04,0x00', next);
      console.log('command sent');
    },
    function(data, next) {
      console.log('initiating read...');
      usb.read(next);
    },
    function(data) {
      console.log('read output from stick: ' + data);
      Jockey.send('message', {message: 'read done: ' + data});
    }
  ],
  function(err) {
    console.log('error: ' + err);
    Jockey.send('message', {message: 'error: ' + err});
  });
});