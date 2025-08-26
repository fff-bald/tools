package process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SQLScriptExecutor extends JFrame {
    private JTextField hostField, portField, databaseField, usernameField;
    private JPasswordField passwordField;
    private JTextArea logArea;
    private JButton selectFileButton, executeButton, saveConfigButton;
    private File selectedSqlFile;

    // 配置文件名
    private static final String CONFIG_FILE = "db_config.properties";

    public SQLScriptExecutor() {
        initializeUI();
        loadDatabaseConfig(); // 启动时加载配置
    }

    private void initializeUI() {
        setTitle("SQL脚本执行器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建数据库连接配置面板
        JPanel configPanel = createConfigPanel();
        add(configPanel, BorderLayout.NORTH);

        // 创建文件选择和执行按钮面板
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.CENTER);

        // 创建日志显示区域
        JPanel logPanel = createLogPanel();
        add(logPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("数据库连接配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 主机地址
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("主机地址:"), gbc);
        gbc.gridx = 1;
        hostField = new JTextField("localhost", 15);
        panel.add(hostField, gbc);

        // 端口
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 3;
        portField = new JTextField("3306", 8);
        panel.add(portField, gbc);

        // 数据库名
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("数据库名:"), gbc);
        gbc.gridx = 1;
        databaseField = new JTextField(15);
        panel.add(databaseField, gbc);

        // 用户名
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 3;
        usernameField = new JTextField(8);
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // 保存配置按钮
        gbc.gridx = 2;
        gbc.gridy = 2;
        saveConfigButton = new JButton("保存配置");
        saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDatabaseConfig();
            }
        });
        panel.add(saveConfigButton, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        selectFileButton = new JButton("选择SQL文件");
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSqlFile();
            }
        });

        executeButton = new JButton("执行脚本");
        executeButton.setEnabled(false);
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeScript();
            }
        });

        panel.add(selectFileButton);
        panel.add(executeButton);
        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("执行日志"));

        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 从本地文件加载数据库配置
     */
    private void loadDatabaseConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            logMessage("配置文件不存在，使用默认配置");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);

            // 读取配置并设置到输入框
            String host = props.getProperty("host", "localhost");
            String port = props.getProperty("port", "3306");
            String database = props.getProperty("database", "");
            String username = props.getProperty("username", "");
            String password = props.getProperty("password", "");

            hostField.setText(host);
            portField.setText(port);
            databaseField.setText(database);
            usernameField.setText(username);
            passwordField.setText(password);

            logMessage("已从配置文件加载数据库连接信息");

        } catch (IOException e) {
            logMessage("读取配置文件失败: " + e.getMessage());
        }
    }

    /**
     * 保存数据库配置到本地文件
     */
    private void saveDatabaseConfig() {
        Properties props = new Properties();
        props.setProperty("host", hostField.getText().trim());
        props.setProperty("port", portField.getText().trim());
        props.setProperty("database", databaseField.getText().trim());
        props.setProperty("username", usernameField.getText().trim());
        props.setProperty("password", new String(passwordField.getPassword()));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "数据库连接配置文件");
            logMessage("配置信息已保存到文件: " + CONFIG_FILE);
            JOptionPane.showMessageDialog(this, "配置保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            String errorMsg = "保存配置文件失败: " + e.getMessage();
            logMessage(errorMsg);
            JOptionPane.showMessageDialog(this, errorMsg, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectSqlFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".sql");
            }

            @Override
            public String getDescription() {
                return "SQL脚本文件 (*.sql)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedSqlFile = fileChooser.getSelectedFile();
            executeButton.setEnabled(true);
            logMessage("已选择文件: " + selectedSqlFile.getAbsolutePath());
        }
    }

    private void executeScript() {
        if (selectedSqlFile == null) {
            JOptionPane.showMessageDialog(this, "请先选择SQL文件！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 验证数据库连接参数
        if (databaseField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整的数据库连接信息！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 在后台线程中执行，避免阻塞UI
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                executeScriptInBackground();
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }
        };
        worker.execute();
    }

    private void executeScriptInBackground() {
        Connection connection = null;
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            logMessage("MySQL驱动加载成功");

            // 构建数据库连接URL
            String url = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
                    hostField.getText().trim(),
                    portField.getText().trim(),
                    databaseField.getText().trim());

            logMessage("正在连接数据库...");

            // 建立数据库连接
            connection = DriverManager.getConnection(url,
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword()));

            logMessage("数据库连接成功！");

            // 读取SQL文件内容
            String sqlContent = readSqlFile(selectedSqlFile);
            logMessage("SQL文件读取完成，开始执行脚本...");

            // 执行SQL脚本
            executeSqlScript(connection, sqlContent);

            logMessage("SQL脚本执行完成！");

            // 执行成功后自动保存配置
            SwingUtilities.invokeLater(() -> {
                saveDatabaseConfig();
            });

            JOptionPane.showMessageDialog(this, "SQL脚本执行成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            String errorMsg = "执行失败: " + e.getMessage();
            logMessage(errorMsg);
            JOptionPane.showMessageDialog(this, errorMsg, "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    logMessage("数据库连接已关闭");
                } catch (SQLException e) {
                    logMessage("关闭数据库连接时出错: " + e.getMessage());
                }
            }
        }
    }

    private String readSqlFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private void executeSqlScript(Connection connection, String sqlContent) throws SQLException {
        // 将SQL内容按分号分割成多个语句
        String[] sqlStatements = sqlContent.split(";");

        Statement statement = connection.createStatement();
        int executedCount = 0;

        for (String sql : sqlStatements) {
            sql = sql.trim();
            if (!sql.isEmpty() && !sql.startsWith("--") && !sql.startsWith("/*")) {
                try {
                    statement.execute(sql);
                    executedCount++;
                    if (executedCount % 10 == 0) {
                        logMessage("已执行 " + executedCount + " 条SQL语句...");
                    }
                } catch (SQLException e) {
                    logMessage("执行SQL语句失败: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    logMessage("错误信息: " + e.getMessage());
                    // 可以选择继续执行或者抛出异常
                    // throw e; // 如果希望遇到错误就停止执行，取消注释这行
                }
            }
        }

        statement.close();
        logMessage("总共执行了 " + executedCount + " 条SQL语句");
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + new java.util.Date() + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new SQLScriptExecutor().setVisible(true);
        });
    }
}