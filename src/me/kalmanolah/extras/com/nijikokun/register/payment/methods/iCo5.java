package me.kalmanolah.extras.com.nijikokun.register.payment.methods;

import me.kalmanolah.extras.com.nijikokun.register.payment.Method;

import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.BankAccount;
import com.iConomy.system.Holdings;
import com.iConomy.util.Constants;

public class iCo5 implements Method {
	private iConomy iConomy;

	public iConomy getPlugin() {
		return iConomy;
	}

	public String getName() {
		return "iConomy";
	}

	public String getVersion() {
		return "5";
	}

	public String format(double amount) {
		return iConomy.format(amount);
	}

	public boolean hasBanks() {
		return Constants.Banking;
	}

	public boolean hasBank(String bank) {
		return (!hasBanks()) ? false : iConomy.Banks.exists(bank);
	}

	public boolean hasAccount(String name) {
		return iConomy.hasAccount(name);
	}

	public boolean hasBankAccount(String bank, String name) {
		return (!hasBank(bank)) ? false : iConomy.getBank(bank).hasAccount(name);
	}

	public MethodAccount getAccount(String name) {
		return new iCoAccount(iConomy.getAccount(name));
	}

	public MethodBankAccount getBankAccount(String bank, String name) {
		return new iCoBankAccount(iConomy.getBank(bank).getAccount(name));
	}

	public boolean isCompatible(Plugin plugin) {
		return plugin.getDescription().getName().equalsIgnoreCase("iconomy") && plugin.getClass().getName().equals("com.iConomy.iConomy") && (plugin instanceof iConomy);
	}

	public void setPlugin(Plugin plugin) {
		iConomy = (iConomy) plugin;
	}
	public class iCoAccount implements MethodAccount {
		private Account account;
		private Holdings holdings;

		public iCoAccount(Account account) {
			this.account = account;
			holdings = account.getHoldings();
		}

		public Account getiCoAccount() {
			return account;
		}

		public double balance() {
			return holdings.balance();
		}

		public boolean set(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.set(amount);
			return true;
		}

		public boolean add(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.add(amount);
			return true;
		}

		public boolean subtract(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.subtract(amount);
			return true;
		}

		public boolean multiply(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.multiply(amount);
			return true;
		}

		public boolean divide(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.divide(amount);
			return true;
		}

		public boolean hasEnough(double amount) {
			return holdings.hasEnough(amount);
		}

		public boolean hasOver(double amount) {
			return holdings.hasOver(amount);
		}

		public boolean hasUnder(double amount) {
			return holdings.hasUnder(amount);
		}

		public boolean isNegative() {
			return holdings.isNegative();
		}

		public boolean remove() {
			if (account == null) {
				return false;
			}
			account.remove();
			return true;
		}
	}
	public class iCoBankAccount implements MethodBankAccount {
		private BankAccount account;
		private Holdings holdings;

		public iCoBankAccount(BankAccount account) {
			this.account = account;
			holdings = account.getHoldings();
		}

		public BankAccount getiCoBankAccount() {
			return account;
		}

		public String getBankName() {
			return account.getBankName();
		}

		public int getBankId() {
			return account.getBankId();
		}

		public double balance() {
			return holdings.balance();
		}

		public boolean set(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.set(amount);
			return true;
		}

		public boolean add(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.add(amount);
			return true;
		}

		public boolean subtract(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.subtract(amount);
			return true;
		}

		public boolean multiply(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.multiply(amount);
			return true;
		}

		public boolean divide(double amount) {
			if (holdings == null) {
				return false;
			}
			holdings.divide(amount);
			return true;
		}

		public boolean hasEnough(double amount) {
			return holdings.hasEnough(amount);
		}

		public boolean hasOver(double amount) {
			return holdings.hasOver(amount);
		}

		public boolean hasUnder(double amount) {
			return holdings.hasUnder(amount);
		}

		public boolean isNegative() {
			return holdings.isNegative();
		}

		public boolean remove() {
			if (account == null) {
				return false;
			}
			account.remove();
			return true;
		}
	}
}
