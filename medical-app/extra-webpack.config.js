module.exports = {
  module: {
    rules: [
      {
        test: /\.less$/,
        loader: 'less-loader',
        options: {
          lessOptions: {
            modifyVars: {
              'primary-color': '#27ae60',
              'link-color': '#27ae60',
              'border-radius-base': '6px',
              'menu-item-active-bg': '#27ae60'
            },
            javascriptEnabled: true,
          },
        },
      },
    ],
  },
};
