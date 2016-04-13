<?php
	
	/**
	 * Imports
	 */
	require_once '../connect/db_functions.php';
    require_once '../utils/security.php';
    require_once '../constants/errors.php';
	require_once '../constants/preferences.php';
	
	// Error reporting false
	error_reporting(0);
	
	$file = $_POST[FILE];
	$date = $_POST[DATE];
	$auth = $_POST[AUTH];
	
	$sec = new Security();
	
	if (!$sec->checkCredentials($date, $auth)) {
		exit();
	}
	
	if (file_exists($file)) {
		header('Content-Description: File Transfer');
		header('Content-Type: application/octet-stream');
		header('Content-Disposition: attachment; filename="'.basename($file).'"');
		header('Expires: 0');
		header('Cache-Control: must-revalidate');
		header('Pragma: public');
		header('Content-Length: ' . filesize($file));
		readfile($file);
		exit;
	} else {
		echo ERROR_FILE;
	}
	
?>