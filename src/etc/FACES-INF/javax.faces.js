 
/* Object Extensions
 ***********************************************************/
Object.extend(String.prototype, {
	trim: function() {
		var s = this.replace( /^\s+/g, "" );
  		return s.replace( /\s+$/g, "" );
	}
});
Object.extend(Element, {
  replace: function(dest, src)  {
    var d = $(dest);
	var parent = d.parentNode;
	var temp = document.createElement('div');
	temp.innerHTML = src
	parent.replaceChild(temp.firstChild,d);
  },
  serialize: function(e) {
	  var str = (e.xml) ? this.serializeIE(e) : this.serializeMozilla(e);
	  return str;
  },
  serializeIE: function(e) {
	  return e.xml;
  },
  serializeMozilla: function(e) {
	  return new XMLSerializer().serializeToString(e);
  },
  firstElement: function(e) {
	  var first = e.firstChild;
	  while (first && first.nodeType != 1) {
		  first = first.nextSibling;
	  }
	  return first;
  }
});
function $Error(e) {
	if (e.message) {
		return '['+e.lineNumber+'] ' + e.message;
	} else {
		return e.toString();
	}
};
function $Form(src) {
	if (src) {
		var form = $(src);
		while (form && form.tagName && form.tagName.toLowerCase() != 'form') {
			if (form.form) return form.form;
			if (form.parentNode) {
				form = form.parentNode;
			} else {
				form = null;
			}
		}
		if (form) return form;
	}
	return document.forms[0];
};

/* Facelet Utility Class
 ***********************************************************/
var Faces = {
	initialized: false,
	create: function() {
		return function() {
	  		if (Faces.initialized) {
      			this.initialize.apply(this, arguments);
	  		} else {
				var args = arguments;
				var me = this;
	  			Event.observe(window,'load',function() {
					Faces.initialized = true;
					me.initialize.apply(me, args);
				},false);
			}
		}
	},
	toArray: function(s,e) {
		if (typeof s == 'string') {
			return s.split((e)?e:' ').map(function(p) { return p.trim(); });
		}
		return s;
	}
};

/* Turn any Element into a Faces.Command
 ***********************************************************/
Faces.Command = Faces.create();
Faces.Command.prototype = {
	initialize: function(action, event, options) {
		var event = (event) || 'click';
		var options = options;
		Event.observe(action,event,function(e) {
			new Faces.Event(action,options);
			Event.stop(e);
			return false;
		},true);
	}
};

/* ViewState Hash over a given Form
 ***********************************************************/
Faces.ViewState = Class.create();
Faces.ViewState.Ignore = ['button','submit','reset','image'];
Faces.ViewState.prototype = {
	initialize: function(form) {
		var e = Form.getElements($(form));
		var t,p;
		for (var i = 0; i < e.length; i++) {
			if (Faces.ViewState.Ignore.indexOf(e[i].type) == -1) {
				t = e[i].tagName.toLowerCase();
				p = Form.Element.Serializers[t](e[i]);
				if (p && p[0].length != 0) {
					if (p[1].constructor != Array) p[1] = [p[1]];
					if (this[p[0]]) { this[p[0]] = this[p[0]].concat(p[1]); }
					else this[p[0]] = p[1];
				}
			}
		};
	},
	toQueryString: function() {
		var q = new Array();
		var i,j,p,v;
		for (property in this) {
			if (this[property]) {
				if (typeof this[property] == 'function') continue;
				p = encodeURIComponent(property);
				if (this[property].constructor == Array) {
					for (j = 0; j < this[property].length; j++) {
						v = this[property][j];
						if (v) {
							v = encodeURIComponent(v);
							q.push(p+'='+v);
						}
					}
				} else {
					v = encodeURIComponent(this[property]);
					q.push(p+'='+v);
				}
			}
		}
		return q.join('&');
	}
};

/* Handles Sending Events to the Server
 ***********************************************************/
Faces.Event = Class.create();
Object.extend(Object.extend(Faces.Event.prototype, Ajax.Request.prototype), {
  initialize: function(source, options) {
    this.transport = Ajax.getTransport();
    this.setOptions(options);
	
	// make sure we are posting
	this.options.method = 'post';
	
	// get form
	this.form = $Form(source);
	this.url = this.form.action;
	
	// create viewState
	var viewState = new Faces.ViewState(this.form);
	
	// add passed parameters
	Object.extend(viewState, this.options.parameters || {});
	this.options.parameters = null;
	
	// add source
	var action = $(source);
	if (action && action.form) viewState[action.name] = action.value;
	else viewState[source] = source;
	
	// initialize headers
	this.options.requestHeaders = this.options.requestHeaders || [];
	
	// guarantee our header
	this.options.requestHeaders.push('javax.faces.Async');
	this.options.requestHeaders.push('true');
	
	// add event
	if (this.options.event) {
	    var sourceId = $(source).id || $(source).name;
		sourceId += "," + this.options.event;
		if (this.options.immediate) {
			sourceId += ",immediate";
		}
		this.options.requestHeaders.push('javax.faces.Event');
		this.options.requestHeaders.push(sourceId);
	}
	
	// add update
	if (this.options.update) {
		this.options.requestHeaders.push('javax.faces.Update');
		this.options.requestHeaders.push(Faces.toArray(this.options.update,',').join(','));
	}
	
	// add encode
	if (this.options.encode) {
		this.options.requestHeaders.push('javax.faces.Encode');
		this.options.requestHeaders.push(Faces.toArray(this.options.encode,',').join(','));
	}
	
	// build url
	//this.url += (this.url.match(/\?/) ? '&' : '?') + viewState.toQueryString();
	this.options.postBody = viewState.toQueryString();

    var onComplete = this.options.onComplete;
    this.options.onComplete = (function(transport, object) {
      this.encodeView();
	  if (onComplete) {
      	onComplete(transport, object);
	  } else {
		 this.evalResponse();
	  }
    }).bind(this);

	if (this.options.onException == null) {
		this.options.onException = this.onException;
	}
	
	// send request
    this.request(this.url);
  },
  encodeView: function() {
	  //alert(this.transport.getAllResponseHeaders());
	  var encode = this.header('javax.faces.Encode');
	  if (encode) {
		  encode = eval(encode);
		  var view;
		  for (var i = 0; i < encode.length; i++) {
		    view = this.header('javax.faces.Encode_' + encode[i]);
			if (view) {
				view.evalScripts();
				view = view.stripScripts();
				Element.replace(encode[i], view);
			}
		  }
	  }
  },
  evalResponse: function() {
	  if (this.responseIsSuccess()) {
		  var text = this.transport.responseText;
		  if (text) {
			  try {
			      text.evalScripts();
			  } catch (e) {}
		  }
	  }
  },
  onException: function(o,e) {
	  throw e;
  }
});

/* Take any Event and delegate it to an Observer
 ***********************************************************/
Faces.Observer = Faces.create();
Faces.Observer.prototype = {
	initialize: function(trigger,events,options) {
		this.options = {};
		Object.extend(this.options, options || {});
		var source = this.options.source;
		var fn = function(e) {
			new Faces.Event((source || Event.element(e)), options);
			Event.stop(e);
			return false;
		};
		var event = events || 'click';
		var ta = Faces.toArray(trigger);
		var ea = Faces.toArray(events);
		for (var i = 0; i < ta.length; i++) {
			for (var j = 0; j < ea.length; j++) {
				Event.observe($(ta[i]),ea[j],fn,true);
			}
		}
	}
};