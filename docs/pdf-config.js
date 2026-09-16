module.exports = {
  dest: 'docs',
  pdf_options: {
    format: 'A4',
    margin: { top: '18mm', bottom: '18mm', left: '16mm', right: '16mm' },
    printBackground: true,
  },
  stylesheet: [],
  body_class: [],
  css: `
    body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 11pt; line-height: 1.45; color: #1f2937; }
    h1 { color: #C41E3A; font-size: 22pt; border-bottom: 2px solid #C41E3A; padding-bottom: 8px; }
    h2 { color: #9B1830; font-size: 14pt; margin-top: 22px; }
    h3 { color: #374151; font-size: 12pt; }
    table { border-collapse: collapse; width: 100%; margin: 12px 0; font-size: 10pt; }
    th, td { border: 1px solid #d1d5db; padding: 6px 8px; text-align: left; }
    th { background: #FEF2F4; }
    code { background: #f3f4f6; padding: 1px 4px; border-radius: 3px; font-size: 9.5pt; }
    hr { border: none; border-top: 1px solid #e5e7eb; margin: 20px 0; }
  `,
};
