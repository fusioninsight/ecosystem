require(["gitbook", "jQuery"], function(gitbook, $) {
  gitbook.events.bind('page.change', function (e, config) {
	var scrolldiv = $('.body-inner')[0].scrollTop;
	var existheader = $('.tg >thead');
	if (existheader.length>=1) {
        var header = $('.tg >thead')[0];
		var sticky = $('.tg >thead').offset().top;
		fixnodes_width = []
		fixnodes =  $('.tg > thead > tr > td')
		for (var k = 0, length = fixnodes.length; k < length; k++) {
			fixnodes_width.push(fixnodes[k].offsetWidth)
		}
		$('.body-inner')[0].onscroll = function() {
		  if ($('.body-inner')[0].scrollTop >= sticky) {
			if (!document.getElementById('clonehead')) {
			  var tg = $('.tg')[0];
			  var newnode = header.cloneNode(true);
			  newnode.classList.add("sticky");
			  newnode.setAttribute("id", "clonehead");
			  tg.insertBefore(newnode, header);
			  var newnode_tds = $('#clonehead > tr > td');
			  for (var k = 0, length = newnode_tds.length; k < length; k++) {
				newnode_tds[k].width = fixnodes_width[k]
			  }
			}
		  } else {
			if (document.getElementById('clonehead')) {
			  document.getElementById('clonehead').remove();
			}
		  }
		};
    } 
  })
})
