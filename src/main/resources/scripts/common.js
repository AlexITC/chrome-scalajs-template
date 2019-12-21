/**
 * Expose functions that are difficult to represent in Scala.
 *
 * The idea is to create a facade that simplifies them to our needs.
 */
var facade = {
  notify: (title, message, iconUrl) => {
    chrome.notifications.create("", { title: title, message: message, type: "basic", iconUrl: iconUrl })
  }
};
