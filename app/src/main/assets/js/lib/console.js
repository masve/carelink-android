if (typeof console  != "undefined")
  if (typeof console.log != 'undefined')
    console.olog = console.log;
else
  console.olog = function() {};

console.log = function(message) {
  console.olog(message);
  $('#debugDiv').append('<p>' + message + '</p>');
};
console.error = console.debug = console.info =  console.log;