var Usb = function(options) {
  var uuid = guid();

  return {
    open: function(callback) {
      var eventHandlerId = 'openComplete-' + uuid;

      Jockey.on(eventHandlerId, function(response) {
          Jockey.off(eventHandlerId);

          response = JSON.parse(response);

          callback(response.err);
      });
      Jockey.send('open', {eventHandlerId: eventHandlerId});
    },
    write: function(message, callback) {
      var eventHandlerId = 'writeComplete-' + uuid;

      Jockey.on(eventHandlerId, function(response) {
          Jockey.off(eventHandlerId);

          response = JSON.parse(response);

          callback(response.err);
      });
      Jockey.send('write', {eventHandlerId: eventHandlerId, message: message});
    },
    read: function(callback) {
      var eventHandlerId = 'readComplete-' + uuid;

      Jockey.on(eventHandlerId, function(response) {
          Jockey.off(eventHandlerId);

          response = JSON.parse(response);

          callback(response.err, response.data);
      });
      Jockey.send('read', {eventHandlerId: eventHandlerId});
    }
  }
};