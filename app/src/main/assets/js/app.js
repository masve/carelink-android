var usb = Usb();

Jockey.on('send-opcode', function() {
  common.step([
    function(next) {
      usb.open(next);
    },
    function(data, next) {
      usb.write('0x04,0x00', next);
    },
    function(data, next) {
      usb.read(next);
    },
    function(data) {
      Jockey.send('message', {message: 'read done: ' + data});
    }
  ],
  function(err) {
    Jockey.send('message', {message: 'error: ' + err});
  });
});