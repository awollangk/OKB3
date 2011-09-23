package me.kalmanolah.okb3extras.com.nijikokun.register.payment.methods;

import me.kalmanolah.okb3extras.com.nijikokun.register.payment.Method;

import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

/**
 * iConomy 4 Implementation of Method
 * 
 * @author Nijikokun <nijikokun@shortmail.com> (@nijikokun)
 * @copyright (c) 2011
 * @license AOL license <http://aol.nexua.org>
 */
public class iCo4 implements Method {
	private iConomy iConomy;

	public iConomy getPlugin() {
		return iConomy;
	}

	public String getName() {
		return "iConomy";
	}

	public String getVersion() {
		return "4";
	}

	public int fractionalDigits() {
		return 2;
	}

	public String format(double amount) {
		return com.nijiko.coelho.iConomy.iConomy.getBank().format(amount);
	}

	public boolean hasBanks() {
		return false;
	}

	public boolean hasBank(String bank) {
		return false;
	}

	public boolean hasAccount(String name) {
		return com.nijiko.coelho.iConomy.iConomy.getBank().hasAccount(name);
	}

	public boolean hasBankAccount(String bank, String name) {
		return false;
	}

	public MethodAccount getAccount(String name) {
		return new iCoAccount(com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(name));
	}

	public MethodBankAccount getBankAccount(String bank, String name) {
		return null;
	}

	public boolean isCompatible(Plugin plugin) {
		return plugin.getDescription().getName().equalsIgnoreCase("iconomy") && plugin.getClass().getName().equals("com.nijiko.coelho.iConomy.iConomy") && (plugin instanceof iConomy);
	}

	public void setPlugin(Plugin plugin) {
		iConomy = (iConomy) plugin;
	}
	public class iCoAccount implements MethodAccount {
		private Account account;

		public iCoAccount(Account account) {
			this.account = account;
		}

		public Account getiCoAccount() {
			return account;
		}

		public double balance() {
			return account.getBalance();
		}

		public boolean set(double amount) {
			if (account == null) {
				return false;
			}
			account.setBalance(amount);
			return true;
		}

		public boolean add(double amount) {
			if (account == null) {
				return false;
			}
			account.add(amount);
			return true;
		}

		public boolean subtract(double amount) {
			if (account == null) {
				return false;
			}
			account.subtract(amount);
			return true;
		}

		public boolean multiply(double amount) {
			if (account == null) {
				return false;
			}
			account.multiply(amount);
			return true;
		}

		public boolean divide(double amount) {
			if (account == null) {
				return false;
			}
			account.divide(amount);
			return true;
		}

		public boolean hasEnough(double amount) {
			return account.hasEnough(amount);
		}

		public boolean hasOver(double amount) {
			return account.hasOver(amount);
		}

		public boolean hasUnder(double amount) {
			return (balance() < amount);
		}

		public boolean isNegative() {
			return account.isNegative();
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
