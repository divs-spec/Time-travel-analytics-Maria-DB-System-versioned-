import httpProxy from 'http-proxy';

const proxy = httpProxy.createProxyServer();

export const config = {
  api: {
    bodyParser: false,
  },
};

export default function handler(req, res) {
  return new Promise((resolve, reject) => {
    proxy.web(req, res, { target: 'http://backend:8080', changeOrigin: true }, (err) => {
      reject(err);
    });
    proxy.once('proxyRes', () => resolve(true));
  });
}
