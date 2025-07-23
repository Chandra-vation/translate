(function ($, window, document) {
    "use strict";

    const ACTION_NAME = "my-custom-action";
    const SERVLET_URL = "/bin/translatePage";
    
    function addCustomButton() {
        if ($("[data-foundation-collection-action='" + ACTION_NAME + "']").length === 0) {

            const button = new Coral.Button().set({
                icon: "globe",
                variant: "quietaction",
                label: { innerHTML: "Translate..." }
            });

            $(button).attr("data-foundation-collection-action", ACTION_NAME);

            $(`coral-actionbar[data-foundation-collection-actionbar-target='.cq-siteadmin-admin-childpages']`)
                .find("._coral-ActionBar-primary")
                .append(button);

            button.on("click", function () {
                const selections = $(".foundation-selections-item");

                if (selections.length !== 1) {
                    Coral.dialog.alert("Please select exactly one page.");
                    return;
                }

                const selectedPath = selections[0].getAttribute("data-foundation-collection-item-id");

                fetch(`${SERVLET_URL}?path=${encodeURIComponent(selectedPath)}`, {
                    method: "GET",
                    headers: {
                        "Accept": "application/json"
                    }
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error("Servlet error");
                        }
                        return response.json();
                    })
                    .then(data => {
                        Coral.dialog.alert(`Servlet response: ${JSON.stringify(data)}`);
                    })
                    .catch(error => {
                        console.error("Servlet call failed:", error);
                        Coral.dialog.alert("Failed to call servlet.");
                    });
            });
        }
    }

    if (document.querySelector(".foundation-collection-actionbar")) {
        addCustomButton();
    }

    $(document).on("foundation-contentloaded", function () {
        addCustomButton();
    });

})(jQuery, window, document);
