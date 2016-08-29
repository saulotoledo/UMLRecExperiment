$(document).ready(function() {
    
    $(document).on("keydown", function (e) {
        if ((e.which || e.keyCode) == 116) {
            e.preventDefault();
        }
    });
    
    $("form").validate();
    Ladda.bind('button[type=submit]', { timeout: 2000 });
    
    $('#firstFormSubmit').prop("disabled", true);
    
    var checkAgreement = function () {
        if ($('#acceptAgreement').is(":checked")) {
            $('#firstFormSubmit').prop("disabled", false);
        } else {
            $('#firstFormSubmit').prop("disabled", true);
        }
    }
    checkAgreement();
    
    $('#acceptAgreement').change(checkAgreement);
});

function toggleStartAgainLink() {
    $('#final-link').toggle();
}
