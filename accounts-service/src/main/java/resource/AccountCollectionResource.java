package resource;

import dao.AccountsDAO;
import domain.Account;
import domain.ErrorMessage;
import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Status;

public class AccountCollectionResource extends Jooby {

	public AccountCollectionResource(AccountsDAO dao) {

		path("/api/accounts", () -> {

			/**
			 * Get all registered accounts.
			 */
			get(() -> {
				return dao.getAll();
			});

			/**
			 * Create a new account.
			 */
			post((req, rsp) -> {

				Account account = req.body(Account.class);

				System.out.println(account);

				String uri = req.path() + "/account/" + account.getId();

				account.setUri(uri);

				if (dao.exists(account.getId())) {
					rsp.status(Status.UNPROCESSABLE_ENTITY).send(new ErrorMessage("There is already a customer account with that ID."));
				} else {
					dao.save(account);
					rsp.status(Status.CREATED).send(account);
				}

			});

		}).produces(MediaType.json).consumes(MediaType.json);

	}

}
