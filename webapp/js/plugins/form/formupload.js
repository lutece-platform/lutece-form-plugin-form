var uploading = 0;
var baseUrl = document.getElementsByTagName("base")[0].href;
function addAsynchronousUploadField(fieldId) {
	var flashVersion = swfobject.getFlashPlayerVersion();
	/* Flash Player 9.0.24 or greater  - simple mode otherwise */
	if ( swfobject.hasFlashPlayerVersion( "9.0.24" ) )
	{
		$("#_form_upload_submit_" + fieldId).hide();
	    $('#' + fieldId).uploadify({
	        'uploader' : 'js/plugins/form/uploadify/swf/uploadify.swf',
	        'script' : baseUrl + '/jsp/site/upload',
	        'cancelImg' : 'js/plugins/form/uploadify/cancel.png',
			'auto' : true,
			'buttonText' : 'Parcourir',
			'displayData' : 'percentage',
			
			// additional parameters
			'scriptData' : {'jsessionid' : document.cookie.match(/JSESSIONID=([^;]+)/)[1], 'page': 'form', 'fieldname':fieldId},
			
			// event handlers
			'onComplete' : function(event,ID,fileObj,data) {
				formOnUploadComplete(event,ID,fileObj,data);
				$('#' + fieldId).uploadifySettings('hideButton',false);
			},
			'onError' : function(event,ID,fileObj,data) {
				handleError( event,ID,fileObj,data,fieldId );
				$('#' + fieldId).uploadifySettings('hideButton',false);
			},
			'onCancel' : function(event,ID,fileObj,data) {
				uploading--;
				$('#' + fieldId).uploadifySettings('hideButton',false);
			},
			'onSelect' : function(event,ID) {
				if ( !formStartUpload( event, ID, fieldId ) )
				{
					return false;
				}
				else
				{
					$('#' + fieldId).uploadifySettings('hideButton',true);
				}
			}
	    });
	    
	    /* move form help so the question mark is besides the input instead of below */
	    var formHelp = $( '#' + fieldId ).parent(  ).parent(  ).find( '.form-help' );
		var formQueue = $( '#' + fieldId + 'Queue' );
		formQueue.appendTo( formHelp );
	    
	    /* removing checkbox, replaced by a link */
	    if ( $( '#delete_' + fieldId ) )
	    {
	    	$( '#delete_' + fieldId ).hide(  );
	    	var fileName = $( '#_filename_' + fieldId + ' input[type="hidden"]' ).val(  );
	    	if ( fileName )
	    	{
	    		var anchorId = '_img_remove_file_' + fieldId;
	    		$( '#_filename_' + fieldId).append( getImageRemoveFile( anchorId, fieldId ) );
	    		$( '#' + anchorId).click( 
    				function( event ) {
						var jsonData = { 'id_entry' : fieldId };
						$.getJSON( baseUrl + '/jsp/site/plugins/form/DoRemoveFile.jsp', jsonData,
								function( json ) {
							$( '#_filename_' + fieldId).hide(  );
						} );
						event.preventDefault();
						$("#_filename_" + fieldId).html( "-" );
    				}
	    		);
	    	}
	    }
	}
}

function canUploadFile( fieldId )
{
	// return true since onSelect does not work properly...
	return true;
	/* var filesCount = getUploadedFilesCount( fieldId );
	var maxFiles = getMaxUploadFiles( fieldId )
	return maxFiles == 0 ? true : filesCount < maxFiles; */
}

/**
 * Handles error
 * @param event event
 * @param ID id
 * @param fileObj  fileObj
 * @param data data
 * @param fieldId fieldId
 */
function handleError( event,ID,fileObj,data,fieldId ) {
	$('#' + fieldId).uploadifyCancel(ID);
	
	if ( data.type=="File Size" ) {
		var maxSize = data.info / 1024;
		var strMaxSize;
		
		if ( maxSize > 1024 )
		{
			maxSize = Math.round( maxSize / 1024 * 100 ) / 100;
			
			strMaxSize = maxSize + "Mo";
		}
		else
		{
			strMaxSize = Math.round( maxSize * 100 ) / 100 + "ko";
		}
		alert("Le fichier est trop gros. La taille est limit�e � " + strMaxSize );
	}
	else
	{
		alert("Une erreur s'est produite lors de l'envoi du fichier : " + data.info );
	}
}

function formStartUpload( event, ID, fieldId )
{
	if( ! canUploadFile( fieldId ) )
	{
		$('#' + fieldId).uploadifyCancel(ID);
		return false;
	}
	uploading++;
	
	return true;
}

/**
 * Called when the upload if successfully completed
 * @param event event
 * @param ID id
 * @param fileObj fileObj
 * @param data data (json)
 */
function formOnUploadComplete(event,ID,fileObj,data)
{
	uploading--;
	
	var jsonData;
	try
	{
		jsonData = $.parseJSON(data);
	}
	catch ( err )
	{
		/* webapp conf problem : probably file upload limit */
		alert("Une erreur est survenue lors de l'envoi du fichier.");
		return;
	}
	
	
	if ( jsonData.error != null )
	{
		alert( jsonData.error );
	}
	
	formDisplayUploadedFiles( jsonData );
}

/**
 * Sets the files list
 * @param jsonData data
 */
function formDisplayUploadedFiles( jsonData )
{
	// create the div
	var fieldName = jsonData.field_name;
	
	if ( fieldName != null )
	{
			displayFile( jsonData.files[0].fileName, fieldName );
	}
}

// add asynchronous behaviour to inputs type=file
$('input[type=file]').each(function(index) {
	addAsynchronousUploadField(this.id);
});

// prevent user from quitting the page before his upload ended.
$('input[type=submit]').each(function() {
	$(this).click(function(event) {
			if ( uploading != 0 )
			{
				event.preventDefault();
				alert('Merci de patienter pendant l\'envoi du fichier');
			}
	});
});

function displayFile( fileName, fieldId )
{
	var anchorId = '_img_remove_file_' + fieldId;
	var strContent =  fileName + "&nbsp; - " + getImageRemoveFile( anchorId, fieldId );
	$("#_filename_" + fieldId).html( strContent );
	$("#" + anchorId).click( 
			function( event ) {
				var jsonData = {"id_entry":fieldId };
				$.getJSON(baseUrl + '/jsp/site/plugins/form/DoRemoveFile.jsp', jsonData,
					function(json) {
					$("#_filename_" + fieldId).hide(  );
					}
				);				
				event.preventDefault();
				$("#_filename_" + fieldId).html( "-" );
			}
	);
}

function getImageRemoveFile( anchorId, fieldId )
{
	return '<a href="#" id="'+ anchorId + '"><img src="images/local/skin/plugins/form/cancel.png" title="Supprimer" alt="Supprimer" /></a>';
}

function keepAlive(  ) {
	if ( uploading > 0 )
	{
		$.getJSON(baseUrl + 'jsp/site/plugins/form/KeepAlive.jsp');
	}
	setTimeout("keepAlive()", 240000);
}

keepAlive(  );
