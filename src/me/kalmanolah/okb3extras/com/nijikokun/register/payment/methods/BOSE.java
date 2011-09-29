package me.kalmanolah.okb3extras.com.nijikokun.register.payment.methods;

import me.kalmanolah.okb3extras.com.nijikokun.register.payment.Method;

import org.bukkit.plugin.Plugin;

import cosine.boseconomy.BOSEconomy;

public class BOSE implements Method {
	private BOSEconomy BOSEconomy;

	public BOSEconomy getPlugin() {
		return BOSEconomy;
	}

	public String getName() {
		return "BOSEconomy";
	}

	public String getVersion() {
		return "0.6.2";
	}

	public String format(double amount) {
		String currency = BOSEconomy.getMoneyNamePlural();
		if (amount == 1) {
			currency = BOSEconomy.getMoneyName();
		}
		return amount + " " + currency;
	}

	public boolean hasBanks() {
		return true;
	}

	public boolean hasBank(String bank) {
		return BOSEconomy.bankExists(bank);
	}

	public boolean hasAccount(String name) {
		return BOSEconomy.playerRegistered(name, false);
	}

	public boolean hasBankAccount(String bank, String name) {
		return BOSEconomy.isBankOwner(bank, name);
	}

	public MethodAccount getAccount(String name) {
		if (!hasAccount(name)) {
			return null;
		}
		return new BOSEAccount(name, BOSEconomy);
	}

	public MethodBankAccount getBankAccount(String bank, String name) {
		return new BOSEBankAccount(bank, name, BOSEconomy);
	}

	public boolean isCompatible(Plugin plugin) {
		return plugin.getDescription().getName().equalsIgnoreCase("boseconomy") && (plugin instanceof BOSEconomy);
	}

	public void setPlugin(Plugin plugin) {
		BOSEconomy = (BOSEconomy) plugin;
	}
	public class BOSEAccount implements MethodAccount {
		private String name;
		private BOSEconomy BOSEconomy;

		public BOSEAccount(String name, BOSEconomy bOSEconomy) {
			this.name = name;
			BOSEconomy = bOSEconomy;
		}

		@SuppressWarnings("deprecation")
		public double balance() {
			return Double.valueOf(BOSEconomy.getPlayerMoney(name));
		}

		@SuppressWarnings("deprecation")
		public boolean set(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			return BOSEconomy.setPlayerMoney(name, IntAmount, false);
		}

		@SuppressWarnings("deprecation")
		public boolean add(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			return BOSEconomy.addPlayerMoney(name, IntAmount, false);
		}

		@SuppressWarnings("deprecation")
		public boolean subtract(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setPlayerMoney(name, (balance - IntAmount), false);
		}

		@SuppressWarnings("deprecation")
		public boolean multiply(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setPlayerMoney(name, (balance * IntAmount), false);
		}

		@SuppressWarnings("deprecation")
		public boolean divide(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setPlayerMoney(name, (balance / IntAmount), false);
		}

		public boolean hasEnough(double amount) {
			return (balance() >= amount);
		}

		public boolean hasOver(double amount) {
			return (balance() > amount);
		}

		public boolean hasUnder(double amount) {
			return (balance() < amount);
		}

		public boolean isNegative() {
			return (balance() < 0);
		}

		public boolean remove() {
			return false;
		}
	}
	public class BOSEBankAccount implements MethodBankAccount {
		private String bank;
		private String name;
		private BOSEconomy BOSEconomy;

		public BOSEBankAccount(String bank, String name, BOSEconomy bOSEconomy) {
			this.name = name;
			this.bank = bank;
			BOSEconomy = bOSEconomy;
		}

		public String getBankName() {
			return bank;
		}

		public int getBankId() {
			return -1;
		}

		@SuppressWarnings("deprecation")
		public double balance() {
			return Double.valueOf(BOSEconomy.getBankMoney(name));
		}

		@SuppressWarnings("deprecation")
		public boolean set(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			return BOSEconomy.setBankMoney(name, IntAmount, true);
		}

		@SuppressWarnings("deprecation")
		public boolean add(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setBankMoney(name, (balance + IntAmount), false);
		}

		@SuppressWarnings("deprecation")
		public boolean subtract(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setBankMoney(name, (balance - IntAmount), false);
		}

		@SuppressWarnings("deprecation")
		public boolean multiply(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setBankMoney(name, (balance * IntAmount), false);
		}

		@SuppressWarnings("deprecation")
		public boolean divide(double amount) {
			int IntAmount = (int) Math.ceil(amount);
			int balance = (int) balance();
			return BOSEconomy.setBankMoney(name, (balance / IntAmount), false);
		}

		public boolean hasEnough(double amount) {
			return (balance() >= amount);
		}

		public boolean hasOver(double amount) {
			return (balance() > amount);
		}

		public boolean hasUnder(double amount) {
			return (balance() < amount);
		}

		public boolean isNegative() {
			return (balance() < 0);
		}

		public boolean remove() {
			return BOSEconomy.removeBank(bank);
		}
	}

	@Override
	public int fractionalDigits() {
		// TODO Auto-generated method stub
		return 0;
	}
}