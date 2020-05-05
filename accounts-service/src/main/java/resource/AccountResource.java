package resource;

import dao.AccountsDAO;
import domain.Account;
import domain.ErrorMessage;
import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Status;

public class AccountResource extends Jooby {

	public AccountResource(AccountsDAO dao) {

		path("/api/accounts/account", () -> {

			use("/:id", (req, rsp, chain) -> {

				String id = req.param("id").value();

				if (!dao.exists(id)) {
					rsp.status(Status.NOT_FOUND).send(new ErrorMessage("There is no customer with that ID."));
				} else {
					chain.next(req, rsp);
				}

			});

			/**
			 * Update existing account.
			 */
			put("/:id", (req, rsp) -> {

				String id = req.param("id").value();

				Account account = req.body().to(Account.class);

				if (!id.equals(account.getId())) {
					rsp.status(Status.CONFLICT).send(new ErrorMessage("Account ID can not be modified via this operation."));
				} else {
					dao.update(id, account);
					rsp.send(account);
				}
			});

			/**
			 * Delete existing account.
			 */			
			delete("/:id", (req, rsp) -> {
				String id = req.param("id").value();
				dao.delete(id);
				rsp.status(Status.NO_CONTENT);
			});

		}).produces(MediaType.json).consumes(MediaType.json);
	}
}
