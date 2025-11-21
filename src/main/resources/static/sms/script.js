$(document).ready(function() {

	function getSMS() {
		return $("textarea").val().trim()
	}
	
	function getGuess() {
		return $("input[name='guess']:checked").val().trim()
	}
	
	function cleanResult() {
		$("#result").removeClass("correct")
		$("#result").removeClass("incorrect")
		$("#result").removeClass("error")
		$("#result").html()
	}

	$("#send-button").click(function (e) {
		e.stopPropagation()
		e.preventDefault()

		var sms = getSMS()
		var guess = getGuess()
		
		$.ajax({
			type: "POST",
			url: "./",
			data: JSON.stringify({"sms": sms, "guess": guess}),
			contentType: "application/json",
			dataType: "json",
			success: handleResult,
			error: handleError	
		})
	})

	function handleResult(res) {
		var wasRight = res.result == getGuess()

		cleanResult()		
		$("#result").addClass(wasRight ? "correct" : "incorrect")
		$("#result").html("The classifier " + (wasRight ? "agrees" : "disagrees"))		
		$("#result").show()
	}

	$("#good-sentence-button").click(function (e) {
		e.stopPropagation()
		e.preventDefault()
		
		$.ajax({
			type: "GET",
			url: "./goodsentence",
			success: handleGoodSentenceResult,
			error: handleError	
		})
	})

	function handleGoodSentenceResult(res) {
		$("textarea").val(res.goodSentence)
		$("#good-sentence-origin").html(`This good sentence was brought to you by the ${res.name} library, version ${res.version}`)
		$("#good-sentence-origin").show()
	}
	
	function handleError(e) {
		cleanResult()		
		$("#result").addClass("error")
		$("#result").html("An error occured (see server log).")
		$("#result").show()
	}
	
	$("textarea").on('change textInput input', function(e) {
		$("#result").hide()
		$("#good-sentence-origin").hide()
	})
	
	$("input").click(function(e) {
		$("#result").hide()
	})
})