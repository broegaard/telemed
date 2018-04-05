/**
  This package defines the storage tier, and contains the Facade
  pattern for accessing the XDS (Cross Enterprise Document Sharing)
  repository.

  <p>
    The actual XDS interface is vastly simplied compared to real XDS
    to simply the TeleMed system.

  <h2>XDS</h2>

  <p>
    The general architecture of XDS involves two main databases:
    <ul>
      <li> A Document Repository is responsible for storing documents
      in a transparent, secure, reliable and persistent manner and
      responding to document retrieval requests.
      </li>
      <li>
	A Document Registry is responsible for storing information
	about those documents so that the documents of interest for
	the care of a patient may be easily found, selected and
	retrieved irrespective of the repository where they are
	actually stored.
      </li>
    </ul>
    Thus a single (National) Registry may server a large set of
    (Regional) Repositories, allowing any client to browse and access
    any document.
  <p>
    As a helpful metaphor, consider Google as the registry and all the
    web servers storing actual documents as the repositories.

  <p>
    Consult <a href="http://wiki.ihe.net/index.php?title=Cross-Enterprise_Document_Sharing">Cross-Enterprise
    Document Sharing wiki</a> for further information.
*/
package telemed.storage;